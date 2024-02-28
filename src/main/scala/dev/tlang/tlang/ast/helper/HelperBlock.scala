package dev.tlang.tlang.ast.helper

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent, DomainBlock}

case class HelperBlock(context: Null, funcs: Option[List[HelperFunc]]) extends DomainBlock with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(getClass)
}
