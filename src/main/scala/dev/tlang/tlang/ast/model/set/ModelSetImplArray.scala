package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.ContextContent

case class ModelSetImplArray(context: Option[ContextContent], var modelSetEntity: Option[ModelSetEntity]) extends ModelSetValueType[ModelSetImplArray] {
  override def getElement: ModelSetImplArray = this

  override def getType: String = "ModelSetImplArray"

  override def getContext: Option[ContextContent] = context
}
