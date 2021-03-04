package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call._
import dev.tlang.tlang.ast.common.value.PrimitiveValue
import dev.tlang.tlang.ast.helper._
import dev.tlang.tlang.ast.model.{ModelBlock, ModelContent}
import dev.tlang.tlang.ast.tmpl.{TmplBlock, TmplBlockAsValue}
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.loader.{BuildModuleTree, Module, Resource}

import scala.collection.mutable.ListBuffer

object ResolveContext {

  def resolveContext(module: Module): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    module.extResources.foreach(_.foreach(module => resolveContext(module._2)))
    module.resources.foreach(resource => {
      val ast = resource._2.ast
      val uses: List[DomainUse] = ast.header match {
        case None => List()
        case Some(header) => header.uses.getOrElse(List())
      }

      ast.body.foreach {
        case HelperBlock(_, funcs) => funcs.foreach(func => extractErrors(errors, BrowseFunc.resolveFuncs(func, module, uses, resource._2)))
        case ModelBlock(_, content) => extractErrors(errors, resolveModel(content, module, uses, resource._2))
        case block: TmplBlock => extractErrors(errors, ResolveTmpl.resolveTmpl(block, module, uses, resource._2))
        case _ => Right(())
      }
    })
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }


  def followCall(resource: Resource, sameResource: Boolean, statements: List[CallObjectType], nextStatement: Int, previousNames: List[String], scope: Scope): Either[List[ResolverError], Option[Value[_]]] = {


    val callName: Option[String] = statements(nextStatement) match {
      case varObj: CallVarObject => Some(varObj.name)
      case funcObj: CallFuncObject => funcObj.name
      case refFuncObj: CallRefFuncObject => refFuncObj.name
      case _ => None
    }

    callName match {
      case Some(objName) =>
        if (!sameResource) {
          resource.ast.header match {
            case Some(header) => header.exposes match {
              case Some(exposes) => exposes.find(_.name == objName) match {
                case Some(expose) => addInScope(expose.name, findInResource(resource, statements(nextStatement)), previousNames, scope)
                case None => Left(List(new ResolverError("Value is not exposed: " + objName)))
              }
              case None => Right(None)
            }
            case None => Right(None)
          }
        } else addInScope(objName, findInResource(resource, statements(nextStatement)), previousNames, scope)
      case None => Left(List(new ResolverError("Should be a var or a func")))
    }

  }

  def findInResource(resource: Resource, nextCaller: CallObjectType): Either[List[ResolverError], Option[Value[_]]] = {
    def browseBody(name: String): Either[List[ResolverError], Option[Value[_]]] = {
      val errors = ListBuffer.empty[ResolverError]
      var elem: Option[Value[_]] = None
      resource.ast.body.foreach {
        case HelperBlock(_, funcs) => if (funcs.isDefined) {
          ResolveUtils.findInFuncs(funcs.get, name) match {
            case Some(func) => elem = Some(func)
            case None =>
          }
        }
        case ModelBlock(_, contents) => if (contents.isDefined) {
          ResolveUtils.findInVars(contents.get, name) match {
            case Some(variable) => elem = Some(variable.value)
            case None =>
          }
        }
        case tmpl: TmplBlock => if (tmpl.name == name) elem = Some(TmplBlockAsValue(tmpl.context, tmpl, Context()))
      }
      if (errors.nonEmpty) Left(errors.toList)
      else Right(elem)
    }

    nextCaller match {
      case CallFuncObject(_, name, _) => browseBody(name.get)
      case CallVarObject(_, name) => browseBody(name)
      case CallRefFuncObject(_, name, _, _) => browseBody(name.get)
      case _ => Right(None)
    }

  }

  def resolveModel(contents: Option[List[ModelContent]], module: Module, uses: List[DomainUse], currentResource: Resource): Either[List[ResolverError], Unit] = {
    contents.foreach(_.foreach {
      //case assign: AssignVar => ResolveStatement.resolveAssignVar(assign, module, uses, scope, currentResource)
      case _ => Right(())
    })
    Right(())
  }

  def addInScope(lastName: String, elem: Either[List[ResolverError], Option[Value[_]]], previousNames: List[String], scope: Scope): Either[List[ResolverError], Option[Value[_]]] = {
    elem match {
      case Left(error) => Left(error)
      case Right(value) => if (value.isDefined) {
        addValueInScope(lastName, value.get, previousNames, scope)
      } else Left(List(ResourceNotFound(value.get.getContext, if (previousNames.nonEmpty) BuildModuleTree.createPkg(previousNames.mkString("/"), lastName) else lastName)))
    }
  }

  def addValueInScope(lastName: String, value: Value[_], previousNames: List[String], scope: Scope): Either[List[ResolverError], Option[Value[_]]] = {
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

  def extractErrors(errors: ListBuffer[ResolverError], result: Either[List[ResolverError], Unit]): Unit = {
    result match {
      case Left(errs) => errors.addAll(errs)
      case Right(_) =>
    }
  }

}
