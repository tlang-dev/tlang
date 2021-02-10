package dev.tlang.tlang.ast.common.value

import io.sorne.tlang.ast.common.call.{CallObject, ComplexValueStatement}
import io.sorne.tlang.interpreter.Value

case class MultiValue(values: List[Value[_]]) extends ComplexValueStatement[MultiValue] {
  override def getValue: MultiValue = this

  override def getType: String = MultiValue.getType

  override def compareTo(value: Value[MultiValue]): Int = 0
}

object MultiValue extends TLangType {
  override def getType: String = "MultiValue"
}
