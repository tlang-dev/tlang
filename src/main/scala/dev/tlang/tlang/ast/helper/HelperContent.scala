package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperContent(context: Option[ContextContent], content: Option[List[HelperStatement]]) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context
}
