package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class ModelSetType(context: Null[ContextContent], `type`: Type) extends ModelSetValueType[ModelSetType] with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getElement: ModelSetType = this

  override def getType: Type = `type`
}
