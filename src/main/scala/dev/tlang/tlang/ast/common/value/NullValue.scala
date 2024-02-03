package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class NullValue[T](context: Option[ContextContent], var value: Option[T], valueType: Option[TLangType]) extends ComplexValueStatement[Option[T]] {
  override def getElement: Option[T] = value

  override def getType: String = if (valueType.isDefined) valueType.get.getType else "LazyValue"

  override def compareTo(comparedVal: Value[Option[T]]): Int = if (this.value.equals(comparedVal.getElement)) 0 else -1

  override def getContext: Option[ContextContent] = context
}

object NullValue {

  val name = "Null"
}