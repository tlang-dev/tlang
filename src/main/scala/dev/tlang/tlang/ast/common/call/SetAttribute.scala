package dev.tlang.tlang.ast.common.call

import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class SetAttribute(context: Null, name: Option[String] = None, value: Operation) extends Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
