package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ModelSetType(context: Null[ContextContent], `type`: String) extends ModelSetValueType[ModelSetType] with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getElement: ModelSetType = this

  override def getType: String = `type`
}
