package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ComplexAttribute(context: Option[ContextContent], attr: Option[String] = None, `type`: Option[String] = None, value: ComplexValueStatement[_]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
