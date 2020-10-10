package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.interpreter.Value

case class ModelNewArrayValue(attr: Option[String], tbl: Option[List[ModelNewAttribute]]) extends ModelNewValueType[ModelNewArrayValue] {
  override def getValue: ModelNewArrayValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ModelNewArrayValue]): Int = ???
}