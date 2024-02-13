package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ModelSetAttribute(context: Null[ContextContent], attr: Option[String], value: ModelSetValueType[_]) extends ModelSetValueType[ModelSetAttribute] with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getElement: ModelSetAttribute = this

  override def getType: String = "ModelSetAttribute[" + value.getType + "]"
}
