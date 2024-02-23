package dev.tlang.tlang.ast.helper

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class HelperContent(context: Null[ContextContent], content: Option[List[HelperStatement]]) extends HelperStatement with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
