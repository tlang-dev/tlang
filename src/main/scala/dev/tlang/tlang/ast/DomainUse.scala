package dev.tlang.tlang.ast

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class DomainUse(context: Option[ContextContent], parts: List[String]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
