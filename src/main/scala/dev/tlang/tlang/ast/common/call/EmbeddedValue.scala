package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class EmbeddedValue(context: Option[ContextContent], value: Value[_]) extends ComplexValueStatement[Value[_]] {
  override def compareTo(value: Value[Value[_]]): Int = 0

  override def getElement: Value[_] = value

  override def getType: String = value.getType

  override def getContext: Option[ContextContent] = context
}
