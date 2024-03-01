package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent, DomainBlock}

case class HelperBlock(context: Option[ContextContent], funcs: Option[List[HelperFunc]]) extends DomainBlock with AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
