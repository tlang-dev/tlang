package dev.tlang.tlang.resolver

import dev.tlang.tlang.ast.DomainUse
import dev.tlang.tlang.ast.common.call.{CallFuncObject, CallObject, CallRefFuncObject, CallVarObject}
import dev.tlang.tlang.ast.helper.HelperFunc
import dev.tlang.tlang.ast.tmpl.TmplBlockAsValue
import dev.tlang.tlang.interpreter.context.Scope
import dev.tlang.tlang.loader.{Module, Resource}
import dev.tlang.tlang.resolver.ResolveContext.{addValueInScope, extractErrors, findInResource, followCall}

import scala.collection.mutable.ListBuffer

object FollowCallObject {

  def followCallObject(call: CallObject, module: Module, uses: List[DomainUse], scope: Scope, currentResource: Resource, newName: Option[String] = None): Either[List[ResolverError], Unit] = {

    def callNextLevel(resource: Resource, sameResource: Boolean, previousNames: List[String], level: Int): Either[List[ResolverError], Unit] = {
      followCall(resource, sameResource, call.statements, level, previousNames, scope) match {
        case Left(error) => Left(error)
        case Right(value) =>
          val stmt = call.statements(level)
          stmt match {
            case funcObject: CallFuncObject => BrowseHelperStatement.browseCallFuncObjectParams(funcObject.currying, value.get, module, uses, scope, currentResource)
            case funcObject: CallRefFuncObject =>
              value.get match {
                case tmpl: TmplBlockAsValue => funcObject.func = Some(Right(tmpl.block))
                case func: HelperFunc => funcObject.func = Some(Left(func))
              }
              BrowseHelperStatement.browseCallFuncObjectParams(funcObject.currying, value.get, module, uses, scope, currentResource)
            case _ => Right(())
          }
      }

    }

    def findOutside(varObj: CallVarObject): Either[List[ResolverError], Unit] = {
      val errors = ListBuffer.empty[ResolverError]
      uses.foreach(use => {
        if (use.parts.last == varObj.name) {
          ResolveUtils.findResource(use, module) match {
            case None => errors.addOne(ResourceNotFound("Cannot find " + use.parts.mkString("/")))
            case Some(resource) =>
              extractErrors(errors, callNextLevel(resource, sameResource = false, List(use.parts.last), 1))
          }
        }
      })
      if (errors.nonEmpty) Left(errors.toList)
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
      case _: CallRefFuncObject =>
        callNextLevel(currentResource, sameResource = true, List(), 0)
      case _ => Right(())
    }

  }

}
