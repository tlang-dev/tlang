package dev.tlang.tlang.ast.common.call

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class CallVarObject(context: Null[ContextContent], name: String) extends CallObjectType with AstContext {
  override def getContext: Null[ContextContent] = context
}
