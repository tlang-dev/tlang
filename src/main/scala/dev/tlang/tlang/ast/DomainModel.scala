package dev.tlang.tlang.ast

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class DomainModel(context: Option[ContextContent], header: Option[DomainHeader], body: List[DomainBlock]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
