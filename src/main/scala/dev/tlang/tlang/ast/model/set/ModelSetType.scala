package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{Context, ContextContent}

case class ModelSetType(context: Null, `type`: Type) extends ModelSetValueType[ModelSetType] with Context {
  override def getContext: Null = context

  override def getType: Type = `type`
}
