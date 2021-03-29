package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class CallFuncObject(context: Option[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]]) extends CallObjectType with AstContext {
  override def getContext: Option[ContextContent] = context
}
