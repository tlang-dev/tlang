package dev.tlang.tlang.ast.model

import dev.tlang.tlang.ast.DomainBlock
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ModelBlock(context: Null[ContextContent], content: Option[List[ModelContent[_]]]) extends DomainBlock with AstContext {
  override def getContext: Null[ContextContent] = context
}
