package io.sorne.tlang.ast.model.`new`

import io.sorne.tlang.interpreter.Value

case class ModelNewPrimitiveValue(attr: Option[String] = None, value: String) extends ModelNewValueType[ModelNewPrimitiveValue] {
  override def getValue: ModelNewPrimitiveValue = this

  override def getType: String = getClass.getName

  override def compareTo(value: Value[ModelNewPrimitiveValue]): Int = ???
}
