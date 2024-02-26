package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ContextContent}

case class ModelSetType(context: Null, `type`: Type) extends ModelSetValueType[ModelSetType] with AstContext {
  override def getContext: Null = context

  override def getType: Type = `type`
}
