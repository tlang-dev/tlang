package dev.tlang.tlang.ast.common.call

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class CallFuncParam(context: Null[ContextContent], params: Option[List[SetAttribute]]) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
