package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.ContextContent

/**
 *
 * @param context
 * @param modelSetEntity points to the parent ModelSetEntity from which this impl is related
 * @param attrs
 */
case class ModelSetImpl(context: Null[ContextContent], var modelSetEntity: Option[ModelSetEntity], attrs: Option[List[ModelSetAttribute]]) extends ModelSetValueType[ModelSetImpl] {
  override def getElement: ModelSetImpl = this

  override def getType: String = "ModelSetImpl"

}
