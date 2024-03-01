package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class HelperCurrying(context: Option[ContextContent], params: List[HelperParam]) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
