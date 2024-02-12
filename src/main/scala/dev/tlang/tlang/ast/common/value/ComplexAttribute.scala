package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.ValueType
import dev.tlang.tlang.ast.common.operation.Operation
import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ComplexAttribute(context: Null[ContextContent], attr: Option[String] = None, `type`: Option[ValueType] = None, value: Operation) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
