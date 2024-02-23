package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallRefFuncObject, CallVarObject}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}
import dev.tlang.tlang.resolver.ResolveContext.{addValueInScope, extractErrors, findInResource, followCall}
import dev.tlang.tlang.tmpl.lang.ast.LangBlockAsValue

import scala.collection.mutable.ListBuffer

object FollowCallObject {

  def followCallObject(call: CallObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource, newName: Option[String] = None): Either[List[ResolverError], Unit] = {

    call.statements.head match {
      case varObj: CallVarObject =>
        findInResource(currentResource, varObj) match {
          case Left(error) => Left(error)
//          case Right(elem) => elem match {
//            case Some(value) => addValueInScope(newName.getOrElse(varObj.name), value, List(), scope) match {
//              case Left(error) =>
//                Left(error)
//              case Right(_) =>
//                Right(())
//            }
//            case None => findOutside(call, varObj, module, uses, scope, currentResource)
//          }
        }


      case _: CallFuncObject =>
        callNextLevel(call, module, uses, scope, currentResource, currentResource, sameResource = true, List(), 0)
      case _: CallRefFuncObject =>
        callNextLevel(call, module, uses, scope, currentResource, currentResource, sameResource = true, List(), 0)
      case _ => Right(())
    }

  }

  private def callNextLevel(call: CallObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource, resource: Resource, sameResource: Boolean, previousNames: List[String], level: Int): Either[List[ResolverError], Unit] = {
    followCall(resource, sameResource, call.statements, level, previousNames, scope) match {
      case Left(error) => Left(error)
      case Right(value) =>
        val stmt = call.statements(level)
        stmt match {
          case funcObject: CallFuncObject =>
            BrowseHelperStatement.browseCallFuncObjectParams(funcObject.currying, value.get, module, uses, scope, currentResource)
          case funcObject: CallRefFuncObject =>
//            value.get match {
////              case tmpl: LangBlockAsValue => funcObject.func = Some(Right(tmpl.block))
////              case func: HelperFunc => funcObject.func = Some(Left(func))
//            }
            BrowseHelperStatement.browseCallFuncObjectParams(funcObject.currying, value.get, module, uses, scope, currentResource)
          case _ => Right(())
        }
    }

  }

  def findOutside(call: CallObject, varObj: CallVarObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    val errors = ListBuffer.empty[ResolverError]
    var outsideFound = false
    uses.foreach(use => {
      if (use.parts.last == varObj.name) {
        ResolveUtils.findResource(use, module) match {
          case None =>
            errors.addOne(ResourceNotFound(use.context, use.parts.mkString("/")))
          case Some(resource) =>
            outsideFound = true
            extractErrors(errors, callNextLevel(call, module, uses, scope, currentResource, resource, sameResource = false, List(use.parts.last), 1))
        }
      }
    })
    if (!outsideFound && call.statements.size > 1)
      verifyParameters(call, varObj, module, uses, scope, currentResource)
    if (errors.nonEmpty) Left(errors.toList)
    else Right(())
  }

  private def verifyParameters(call: CallObject, varObj: CallVarObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource): Either[List[ResolverError], Unit] = {
    if (call.statements.size >= 2 && call.statements.last.isInstanceOf[CallFuncObject]) {
      val callFunc = call.statements.last.asInstanceOf[CallFuncObject]
//      callFunc.currying.foreach(params => params.foreach(param => param.))
////
//      BrowseHelperStatement.browseCallFuncObjectParams(callFunc.currying, call.getElement, module, uses, scope, currentResource)
    }
    Right(())
  }

}
