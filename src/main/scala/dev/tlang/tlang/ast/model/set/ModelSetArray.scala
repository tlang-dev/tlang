package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.ContextContent

case class ModelSetArray(context: Null[ContextContent], array: String) extends ModelSetValueType[ModelSetArray] {

  override def getElement: ModelSetArray = this

  override def getType: String = "ModelSetArray[" + array + "]"
}
