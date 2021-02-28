package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetType(context: Option[ContextContent], `type`: String) extends ModelSetValueType with AstContext {
  override def getContext: Option[ContextContent] = context
}
