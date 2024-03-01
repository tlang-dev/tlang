package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.ContextContent

case class ModelSetType(context: Option[ContextContent], `type`: Type) extends ModelSetValueType[ModelSetType] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = `type`
}
