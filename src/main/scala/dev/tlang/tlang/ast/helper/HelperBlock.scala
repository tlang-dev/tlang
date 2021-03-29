package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.DomainBlock
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperBlock(context: Option[ContextContent], funcs: Option[List[HelperFunc]]) extends DomainBlock with AstContext {
  override def getContext: Option[ContextContent] = context
}
