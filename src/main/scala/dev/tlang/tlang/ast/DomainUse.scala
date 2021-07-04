package dev.tlang.tlang.ast

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class DomainUse(context: Option[ContextContent], parts: List[String], alias: Option[String] = None) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
