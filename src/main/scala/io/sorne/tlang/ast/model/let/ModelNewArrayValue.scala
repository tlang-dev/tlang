package io.sorne.tlang.ast.model.let

import io.sorne.tlang.ast.model.set.ModelSetRefValue
import io.sorne.tlang.interpreter.Value

case class ModelNewArrayValue(attr: Option[String], tbl: Option[List[ModelNewAttribute]]) extends ModelNewValueType[ModelNewArrayValue] with ModelSetRefValue {
  override def getValue: ModelNewArrayValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ModelNewArrayValue]): Int = ???
}
