package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class CallVarObject(context: Option[ContextContent], name: String) extends CallObjectType with AstContext {
  override def getContext: Option[ContextContent] = context
}
