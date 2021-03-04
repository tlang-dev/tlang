package dev.tlang.tlang.resolver.checker

import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.resolver.{ResolverError, TypeError}

object CheckType {

  def checkType(valType: String, value: Value[_]): Either[List[ResolverError], Unit] = {
    if (valType == value.getType) Right(())
    else Left(List(TypeError(value.getContext, value.getType, valType)))
  }

}
