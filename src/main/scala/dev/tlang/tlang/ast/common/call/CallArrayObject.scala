package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class CallArrayObject(context: Null[ContextContent], name: String, position: Operation) extends CallObjectType with AstContext {
  override def getContext: Null[ContextContent] = context
}
