package io.sorne.tlang.ast.common.value

import io.sorne.tlang.ast.common.call.SimpleValueStatement
import io.sorne.tlang.interpreter.Value

case class ArrayValue(tbl: Option[List[SimpleAttribute]]) extends PrimitiveValue[ArrayValue] {
  override def getValue: ArrayValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ArrayValue]): Int = 0
}
