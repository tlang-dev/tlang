package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class ComplexAttribute(context: Null, attr: Option[String] = None, `type`: Option[ValueType] = None, value: Operation) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(getClass)
}
