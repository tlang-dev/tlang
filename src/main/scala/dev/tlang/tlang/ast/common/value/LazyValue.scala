package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.Value
import tlang.internal.ContextContent

case class LazyValue[T <: Value](context: Option[ContextContent], var value: Option[T], valueType: Option[TLangType]) extends ComplexValueStatement[T] {

  //  override def getType: Type = if (valueType.isDefined) valueType.get.getType else ClassType.of(this.getClass)

}
