package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ComplexAttribute(context: Option[ContextContent], attr: Option[String] = None, `type`: Option[String] = None, value: Operation) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
