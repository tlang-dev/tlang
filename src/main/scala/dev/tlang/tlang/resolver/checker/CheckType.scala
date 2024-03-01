package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import dev.tlang.tlang.interpreter.context.{Context, Scope}
import dev.tlang.tlang.resolver.{ResolverError, TypeError}
import dev.tlang.tlang.tmpl.AstValue
import tlang.core.Value

object CheckType {

  def checkType(valType: String, value: AstValue): Either[List[ResolverError], Unit] = {
    if (valType == value.getType) Right(())
//    else Left(List(TypeError(value.getElement.getContext, value.getType.toString, valType)))
    Right(())
  }

  def followCall(valType: String, complexValue: ComplexValueStatement[_], scope: Scope): Either[List[ResolverError], Unit] = {
    complexValue match {
      case callObject: CallObject => FollowCallToTheEnd.followCallToTheEnd(callObject, Context(List(scope))) match {
        case Left(value) => Right(())
        case Right(value) =>Right(())
      }
//      case _ => checkType(valType, complexValue)
    }
  }

}
