package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class LazyValue[T <: Value[T]](context: Option[ContextContent], var value: Option[T], valueType: Option[TLangType]) extends ComplexValueStatement[T] with AstContext {
  override def getElement: T = value.get

  override def getType: String = if (valueType.isDefined) valueType.get.getType else "LazyValue"

  override def compareTo(value: Value[T]): Int = this.value.get.compareTo(value)

  override def getContext: Option[ContextContent] = context
}
