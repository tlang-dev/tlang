package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}

case class NullValue(context: Null, var value: Null, valueType: Option[TLangType]) extends ComplexValueStatement[Null] {

//  override def getType: Type = if (valueType.isDefined) valueType.get.getType else ClassType.of(this.getClass)

}

object NullValue {

  val name = "Null"
}