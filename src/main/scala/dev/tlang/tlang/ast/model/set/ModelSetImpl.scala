package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.ContextContent

/**
 *
 * @param context
 * @param modelSetEntity points to the parent ModelSetEntity from which this impl is related
 * @param attrs
 */
case class ModelSetImpl(context: Option[ContextContent], var modelSetEntity: Option[ModelSetEntity], attrs: Option[List[ModelSetAttribute]]) extends ModelSetValueType[ModelSetImpl] {
  override def getElement: ModelSetImpl = this

  override def getType: String = "ModelSetImpl"

  override def getContext: Option[ContextContent] = context
}
