package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class HelperFor(context: Option[ContextContent], variable: String, start: Option[SimpleValueStatement[_]], forType: ForType.forType, array: SimpleValueStatement[_], body: HelperContent) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context
}
