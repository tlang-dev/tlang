package dev.tlang.tlang.ast

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class DomainHeader(context: Option[ContextContent], exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
