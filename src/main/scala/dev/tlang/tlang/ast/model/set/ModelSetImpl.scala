package dev.tlang.tlang.ast.model.set

import tlang.internal.ContextContent

/**
 *
 * @param context
 * @param modelSetEntity points to the parent ModelSetEntity from which this impl is related
 * @param attrs
 */
case class ModelSetImpl(context: Option[ContextContent], var modelSetEntity: Option[ModelSetEntity], attrs: Option[List[ModelSetAttribute]]) extends ModelSetValueType[ModelSetImpl] {

}
