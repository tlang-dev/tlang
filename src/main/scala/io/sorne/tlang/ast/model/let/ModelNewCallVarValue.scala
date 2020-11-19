package io.sorne.tlang.ast.model.let

import io.sorne.tlang.interpreter.Value

case class ModelNewCallVarValue(name: String) extends ModelNewValueType[ModelNewCallVarValue] {
  override def getValue: ModelNewCallVarValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ModelNewCallVarValue]): Int = this.name.compareTo(value.getValue.name)
}
