package dev.tlang.tlang.ast.common.call

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class CallFuncObject(context: Null[ContextContent], name: Option[String], currying: Option[List[CallFuncParam]]) extends CallObjectType with AstContext {
  override def getContext: Null[ContextContent] = context
}
