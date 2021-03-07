package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetArray(context: Option[ContextContent], array: String) extends ModelSetValueType[ModelSetArray] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getElement: ModelSetArray = this

  override def getType: String = "ModelSetArray[" + array + "]"
}
