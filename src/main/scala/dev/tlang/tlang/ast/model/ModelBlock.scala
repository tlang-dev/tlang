package dev.tlang.tlang.ast.model

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelBlock(context: Option[ContextContent], content: Option[List[ModelContent[_]]]) extends DomainBlock with AstContext {
  override def getContext: Option[ContextContent] = context
}
