package io.sorne.tlang.ast.model.let

import io.sorne.tlang.interpreter.Value

case class ModelNewCallFuncValue(name: String, params: Option[List[Value[_]]]) extends ModelNewValueType[ModelNewCallFuncValue] {
  override def getValue: ModelNewCallFuncValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ModelNewCallFuncValue]): Int = this.name.compareTo(value.getValue.name)
}
