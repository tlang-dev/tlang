package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.ast.model.ModelContent
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetEntity(context: Option[ContextContent], name: String, params: Option[List[ModelSetAttribute]], attrs: Option[List[ModelSetAttribute]]) extends ModelContent with AstContext {
  override def getContext: Option[ContextContent] = context
}
