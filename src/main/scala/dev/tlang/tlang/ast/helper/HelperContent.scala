package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class HelperContent(context: Option[ContextContent], content: Option[List[HelperStatement]]) extends HelperStatement with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
