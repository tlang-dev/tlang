package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallObjectType, CallVarObject}
import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.{ModelBlock, ModelContent}
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.loader.{BuildModuleTree, Module, Resource}

object ResolveContext {

  def resolveContext(module: Module): Either[ResolverError, Unit] = {
    module.resources.foreach(resource => {
      val ast = resource._2.ast
      val uses: List[DomainUse] = ast.header match {
        case None => List()
        case Some(header) => header.uses.getOrElse(List())
      }

      ast.body.foreach {
        case HelperBlock(funcs) => funcs.foreach(resolveFuncs(_, module, uses, resource._2))
        case ModelBlock(content) => resolveModel(content, module, uses, resource._2)
        case _ => Right(())
      }
    })
    Right(())
  }

  def resolveFuncs(funcs: List[HelperFunc], module: Module, uses: List[DomainUse], currentResource: Resource): Either[ResolverError, Unit] = {
    var error: Option[ResolverError] = None
    var i = 0
    while (error.isEmpty && i < funcs.size) {
      ResolveStatement.resolveStatements(funcs(i).block.content, module, uses, funcs(i).scope, currentResource) match {
        case Left(err) => error = Some(err)
        case _ =>
      }
      i += 1
    }
    if (error.isDefined) Left(error.get)
    else Right(())
  }

  def resolveCallObject(call: CallObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource, newName: Option[String] = None): Either[ResolverError, Unit] = {

    def callNextLevel(resource: Resource, sameResource: Boolean, previousNames: List[String], level: Int): Either[ResolverError, Unit] = {
      followCall(resource, sameResource, call.statements, level, previousNames, scope) match {
        case Left(error) => Left(error)
        case Right(value) =>
          val stmt = call.statements(level)
          stmt match {
            case funcObject: CallFuncObject => ResolveStatement.resolveCallFuncObjectParams(funcObject, value.get, module, uses, scope, currentResource)
            case _ => Right(())
          }
      }

    }

    def findOutside(varObj: CallVarObject): Either[ResolverError, Unit] = {
      var error: Option[ResolverError] = None
      var found = false
      var i = 0
      while (!found && error.isEmpty && i < uses.size) {
        val use = uses(i)
        if (use.parts.last == varObj.name) {
          ResolveUtils.findResource(use, module) match {
            case None => error = Some(ResourceNotFound("Cannot find " + use.parts.mkString("/")))
            case Some(resource) =>
              found = true
              callNextLevel(resource, sameResource = false, List(use.parts.last), 1)
          }
        }
        i += 1
      }
      if (error.isDefined) Left(error.get)
      else Right(())
    }

    call.statements.head match {
      case varObj: CallVarObject =>
        findInResource(currentResource, varObj) match {
          case Left(error) => Left(error)
          case Right(elem) => elem match {
            case Some(value) => addValueInScope(newName.getOrElse(varObj.name), value, List(), scope) match {
              case Left(error) => Left(error)
              case Right(value) => Right(())
            }
            case None => findOutside(varObj)
          }
        }

      case _: CallFuncObject =>
        callNextLevel(currentResource, sameResource = true, List(), 0)
      case _ => Right(())
    }

  }

  def followCall(resource: Resource, sameResource: Boolean, statements: List[CallObjectType], nextStatement: Int, previousNames: List[String], scope: Scope): Either[ResolverError, Option[Value[_]]] = {


    val callName: Option[String] = statements(nextStatement) match {
      case varObj: CallVarObject => Some(varObj.name)
      case funcObj: CallFuncObject => funcObj.name
      case _ => None
    }

    callName match {
      case Some(objName) =>
        if (!sameResource) {
          resource.ast.header match {
            case Some(header) => header.exposes match {
              case Some(exposes) => exposes.find(_.name == objName) match {
                case Some(expose) => addInScope(expose.name, findInResource(resource, statements(nextStatement)), previousNames, scope)
                case None => Left(new ResolverError("Value is not exposed: " + objName))
              }
              case None => Right(None)
            }
            case None => Right(None)
          }
        } else addInScope(objName, findInResource(resource, statements(nextStatement)), previousNames, scope)
      case None => Left(new ResolverError("Should be a var or a func"))
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
            ResolveUtils.findInFuncs(funcs.get, name) match {
              case Some(func) => elem = Some(func)
              case None =>
            }
          }
          case ModelBlock(contents) => if (contents.isDefined) {
            ResolveUtils.findInVars(contents.get, name) match {
              case Some(variable) => elem = Some(variable.value)
              case None =>
            }
          }
          case tmpl: TmplBlock => if (tmpl.name == name) elem = Some(TmplBlockAsValue(tmpl, Context()))
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

  def resolveModel(contents: Option[List[ModelContent]], module: Module, uses: List[DomainUse], currentResource: Resource): Either[ResolverError, Unit] = {
    contents.foreach(_.foreach {
      //case assign: AssignVar => ResolveStatement.resolveAssignVar(assign, module, uses, scope, currentResource)
      case _ => Right(())
    })
    Right(())
  }

  def addInScope(lastName: String, elem: Either[ResolverError, Option[Value[_]]], previousNames: List[String], scope: Scope): Either[ResolverError, Option[Value[_]]] = {
    elem match {
      case Left(error) => Left(error)
      case Right(value) => if (value.isDefined) {
        addValueInScope(lastName, value.get, previousNames, scope)
      } else Left(ResourceNotFound("Value is empty"))
    }
  }

  def addValueInScope(lastName: String, value: Value[_], previousNames: List[String], scope: Scope): Either[ResolverError, Option[Value[_]]] = {
    val name = if (previousNames.nonEmpty) BuildModuleTree.createPkg(previousNames.mkString("/"), lastName) else lastName
    val ret = value match {
      case func: HelperFunc => scope.functions.addOne(name, func)
        func
      case variable: PrimitiveValue[_] => scope.variables.addOne(name, variable)
        variable
      case tmpl: TmplBlockAsValue => scope.templates.addOne(name, tmpl.block)
        tmpl
    }
    Right(Some(ret))
  }

}
