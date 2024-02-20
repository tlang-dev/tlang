package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class SetAttribute(context: Null[ContextContent], name: Option[String] = None, value: Operation) extends AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
