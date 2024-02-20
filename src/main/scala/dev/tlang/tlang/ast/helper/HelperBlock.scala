package dev.tlang.tlang.ast.helper

import tlang.core.{Array, Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent, DomainBlock}

case class HelperBlock(context: Null[ContextContent], funcs: Null[Array[HelperFunc]]) extends DomainBlock with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
