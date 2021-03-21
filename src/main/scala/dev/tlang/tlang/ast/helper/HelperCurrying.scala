package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperCurrying(context: Option[ContextContent], params: List[HelperParam]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
