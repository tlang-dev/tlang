package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.Null
import tlang.internal.ContextContent

case class NullValue[T](context: Null[ContextContent], var value: Null[T], valueType: Option[TLangType]) extends ComplexValueStatement[Null[T]] {
  override def getElement: Null[T] = value

  override def getType: String = if (valueType.isDefined) valueType.get.getType else "NullValue"

}

object NullValue {

  val name = "Null"
}