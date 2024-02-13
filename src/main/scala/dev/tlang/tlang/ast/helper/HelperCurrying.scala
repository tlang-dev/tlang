package dev.tlang.tlang.ast.helper

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class HelperCurrying(context: Null[ContextContent], params: List[HelperParam]) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
