package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.ContextContent

case class ModelSetImplArray(context: Null[ContextContent], var modelSetEntity: Option[ModelSetEntity]) extends ModelSetValueType[ModelSetImplArray] {
  override def getElement: ModelSetImplArray = this

  override def getType: String = "ModelSetImplArray"

}
