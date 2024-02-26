package dev.tlang.tlang.ast.helper

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent, DomainBlock}

case class HelperBlock(context: Null, funcs: Option[List[HelperFunc]]) extends DomainBlock with AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(getClass)
}
