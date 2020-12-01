package io.sorne.tlang.resolver

import io.sorne.tlang.ast.DomainUse
import io.sorne.tlang.ast.helper._
import io.sorne.tlang.ast.common.call.{CallFuncObject, CallObject, CallObjectType, CallVarObject}
import io.sorne.tlang.ast.common.value.{AssignVar, EntityValue, PrimitiveValue}
import io.sorne.tlang.ast.model.{ModelBlock, ModelContent}
import io.sorne.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import io.sorne.tlang.interpreter.context.Scope
import io.sorne.tlang.interpreter.{Value, WrongType}
import io.sorne.tlang.loader.{BuildModuleTree, Module, Resource}

object ResolveContext {

  def resolveContext(module: Module): Either[ResolverError, Unit] = {
    module.resources.foreach(resource => {
      val ast = resource._2.ast
      val uses: List[DomainUse] = ast.header match {
        case None => List()
        case Some(header) => header.uses.getOrElse(List())
      }

      ast.body.foreach {
        case HelperBlock(funcs) => funcs.foreach(resolveFuncs(_, module, uses))
        case ModelBlock(content) =>
        case _ =>
      }
    })
    Right(())
  }

  def resolveFuncs(funcs: List[HelperFunc], module: Module, uses: List[DomainUse]): Either[ResolverError, Unit] = {
    var error: Option[ResolverError] = None
    var i = 0
    while (error.isEmpty && i < funcs.size) {
      resolveStatements(funcs(i).block.content, module, uses, funcs(i).scope) match {
        case Left(err) => error = Some(err)
        case _ =>
      }
      i += 1
    }
    if (error.isDefined) Left(error.get)
    else Right(())
  }

  def resolveStatements(statements: Option[List[HelperStatement]], module: Module, uses: List[DomainUse], scope: Scope): Either[ResolverError, Unit] = {
    if (statements.nonEmpty) {
      var error: Option[ResolverError] = None
      var i = 0
      while (error.isEmpty && i < statements.get.size) {
        val statement = statements.get(i)
        statement match {
          case call: CallObject => resolveCallObject(call, module, uses, scope)
          case _ => resolveStatement(statement, module, uses) match {
            case Left(err) => error = Some(err)
            case _ =>
          }
        }
        i += 1
      }
      if (error.isDefined) Left(error.get)
      else Right(())
    } else Right(())
  }

  def resolveCallObject(call: CallObject, module: Module, uses: List[DomainUse], scope: Scope): Either[ResolverError, Unit] = {
    call.statements.head match {
      case varObj: CallVarObject =>
        var error: Option[ResolverError] = None
        var found = false
        var i = 0
        while (!found && error.isEmpty && i < uses.size) {
          val use = uses(i)
          if (use.parts.last == varObj.name) {
            val name = use.parts.mkString("/")
            module.resources.get(name) match {
              case Some(resource) =>
                found = true
                followCall(resource, call.statements, 1, List(use.parts.last), scope)
              case None => module.extResources match {
                case Some(resources) => resources.get(use.parts.head) match {
                  case Some(extModule) => extModule.resources.get(extModule.mainFile) match {
                    case Some(resource) =>
                      found = true
                      followCall(resource, call.statements, 1, List(use.parts.last), scope)
                    case None => Right(())
                  }
                  case None => Right(())
                }
                case None => Right(())
              }
            }
          }
          i += 1
        }
        if (error.isDefined) Left(error.get)
        else Right(())
      case _ => Right(())
    }

  }

  def followCall(resource: Resource, statements: List[CallObjectType], nextStatement: Int, previousNames: List[String], scope: Scope): Either[ResolverError, Unit] = {

    def addInScope(lastName: String, elem: Either[ResolverError, Option[Value[_]]]): Either[ResolverError, Unit] = {
      elem match {
        case Left(error) => Left(error)
        case Right(value) => if (value.isDefined) {
          val name = BuildModuleTree.createPkg(previousNames.mkString("/"), lastName)
          value.get match {
            case func: HelperFunc => scope.functions.addOne(name, func)
            case variable: EntityValue => scope.variables.addOne(name, variable)
            case tmpl: TmplBlockAsValue => scope.templates.addOne(name, tmpl.block)
          }
          Right(())
        } else Right(())
      }
    }

    val callName: Option[String] = statements(nextStatement) match {
      case varObj: CallVarObject => Some(varObj.name)
      case funcObj: CallFuncObject => funcObj.name
      case _ => None
    }

    callName match {
      case Some(objName) => resource.ast.header match {
        case Some(header) => header.exposes match {
          case Some(exposes) => exposes.find(_.name == objName) match {
            case Some(expose) => addInScope(expose.name, findInResource(resource, statements(nextStatement)))
            case None => Right(())
          }
          case None => Right(())
        }
        case None => Right(())
      }
      case None => Left(ResolverError("Should be a var or a func"))
    }

  }

  def findInResource(resource: Resource, nextCaller: CallObjectType): Either[ResolverError, Option[Value[_]]] = {
    def browseBody(name: String): Either[ResolverError, Option[Value[_]]] = {
      var error: Option[ResolverError] = None
      var i = 0
      var elem: Option[Value[_]] = None
      while (elem.isEmpty && error.isEmpty && i < resource.ast.body.size) {
        val block = resource.ast.body(i)
        block match {
          case HelperBlock(funcs) => if (funcs.isDefined) {
            findInFuncs(funcs.get, name) match {
              case Some(func) => elem = Some(func)
              case None =>
            }
          }
          case ModelBlock(contents) => if (contents.isDefined) {
            findInVars(contents.get, name) match {
              case Some(variable) => elem = Some(variable.value)
              case None =>
            }
          }
          case tmpl: TmplBlock => if (tmpl.name == name) elem = Some(TmplBlockAsValue(tmpl, List()))
        }
        i += 1
      }
      if (error.isDefined) Left(error.get)
      else Right(elem)
    }

    nextCaller match {
      case CallFuncObject(name, _) => browseBody(name.get)
      case CallVarObject(name) => browseBody(name)
      case _ => Right(None)
    }

  }

  def findInFuncs(funcs: List[HelperFunc], name: String): Option[HelperFunc] = {
    var i = 0
    var func: Option[HelperFunc] = None
    while (func.isEmpty && i < funcs.size) {
      if (funcs(i).name == name) func = Some(funcs(i))
      i += 1
    }
    func
  }

  def findInVars(contents: List[ModelContent], name: String): Option[AssignVar] = {
    var i = 0
    var entity: Option[AssignVar] = None
    while (entity.isEmpty && i < contents.size) {
      contents(i) match {
        case newEntity: AssignVar => if (newEntity.name == name) entity = Some(newEntity)
        case _ =>
      }
      i += 1
    }
    entity
  }

  def resolveStatement(statement: HelperStatement, module: Module, uses: List[DomainUse]): Either[ResolverError, Unit] = {
    Right(())
  }

  def resolveModel(model: ModelBlock, module: Module, uses: List[DomainUse]): Either[ResolverError, Unit] = {
    Right(())
  }

}
