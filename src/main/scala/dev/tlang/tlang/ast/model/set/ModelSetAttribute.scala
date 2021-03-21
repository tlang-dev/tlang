package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetAttribute(context: Option[ContextContent], attr: Option[String], value: ModelSetValueType[_]) extends ModelSetValueType[ModelSetAttribute] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetAttribute = this

  override def getType: String = "ModelSetAttribute[" + value.getType + "]"
}
