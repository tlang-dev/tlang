package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class CallArrayObject(context: Option[ContextContent], name: String, position: SimpleValueStatement[_]) extends CallObjectType with AstContext {
  override def getContext: Option[ContextContent] = context
}
