package dev.tlang.tlang.ast

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class DomainExpose(context: Option[ContextContent], name: String) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
