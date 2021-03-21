package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetType(context: Option[ContextContent], `type`: String) extends ModelSetValueType[ModelSetType] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetType = this

  override def getType: String = "ModelSetType[" + `type` + "]"
}
