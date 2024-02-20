package dev.tlang.tlang.ast.model

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent, DomainBlock}

case class ModelBlock(context: Null[ContextContent], content: Option[List[ModelContent[_]]]) extends DomainBlock with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
