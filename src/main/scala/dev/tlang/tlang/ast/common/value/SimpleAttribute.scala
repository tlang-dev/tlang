package dev.tlang.tlang.ast.common.value

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class SimpleAttribute(context: Option[ContextContent], attr: Option[String] = None, `type`: Option[String] = None, value: SimpleValueStatement[_]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
