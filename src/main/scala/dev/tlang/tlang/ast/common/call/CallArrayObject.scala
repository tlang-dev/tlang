package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class CallArrayObject(context: Option[ContextContent], name: String, position: Operation) extends CallObjectType with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
