package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class CallArrayObject(context: Null, name: String, position: Operation) extends CallObjectType with AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
