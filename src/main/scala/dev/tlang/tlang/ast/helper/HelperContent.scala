package dev.tlang.tlang.ast.helper

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class HelperContent(context: Null[ContextContent], content: Option[List[HelperStatement]]) extends HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context
}
