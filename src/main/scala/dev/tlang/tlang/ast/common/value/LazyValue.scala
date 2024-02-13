package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class LazyValue[T <: Value[T]](context: Null[ContextContent], var value: Option[T], valueType: Option[TLangType]) extends ComplexValueStatement[T] {
  override def getElement: T = value.get

  override def getType: String = if (valueType.isDefined) valueType.get.getType else "LazyValue"

}
