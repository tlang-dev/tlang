package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class CallFuncParam(context: Option[ContextContent], params: Option[List[SetAttribute]]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
