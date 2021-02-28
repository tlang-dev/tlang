package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetAttribute(context: Option[ContextContent], attr: Option[String], value: ModelSetValueType) extends ModelSetValueType with AstContext {
  override def getContext: Option[ContextContent] = context
}
