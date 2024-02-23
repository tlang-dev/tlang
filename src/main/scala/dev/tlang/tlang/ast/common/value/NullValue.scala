package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class NullValue[T](context: Null[ContextContent], var value: Null[T], valueType: Option[TLangType]) extends ComplexValueStatement[Null[T]] {

//  override def getType: Type = if (valueType.isDefined) valueType.get.getType else ClassType.of(this.getClass)

}

object NullValue {

  val name = "Null"
}