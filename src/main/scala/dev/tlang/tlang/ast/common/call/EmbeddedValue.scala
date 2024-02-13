package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.ClassType
import tlang.core.{Null, Type, Value}
import tlang.internal.ContextContent

case class EmbeddedValue(context: Null[ContextContent], value: Value[_]) extends ComplexValueStatement[Value[_]] {
  override def compareTo(value: Value[Value[_]]): Int = 0

  override def getElement: Value[_] = value

  override def getType: Type = ClassType(this.getClass)

}
