package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.interpreter.Value

case class ArrayValue(tbl: Option[List[SimpleAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getValue: ArrayValue = this

  override def getType: String = ArrayValue.getType

  override def compareTo(value: Value[ArrayValue]): Int = 0
}

object ArrayValue extends TLangType {
  override def getType: String = "ArrayValue"
}
