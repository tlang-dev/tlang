package io.sorne.tlang.ast.model.let

import io.sorne.tlang.interpreter.Value
import io.sorne.tlang.interpreter.`type`.TLangType

case class ModelLetMultiValue(values: List[Value[_]]) extends ModelNewValueType[ModelLetMultiValue] {
  override def getValue: ModelLetMultiValue = this

  override def getType: String = ModelLetMultiValue.getType

  override def compareTo(value: Value[ModelLetMultiValue]): Int = ???
}

object ModelLetMultiValue extends TLangType {
  override def getType: String = "MultiValue"
}
