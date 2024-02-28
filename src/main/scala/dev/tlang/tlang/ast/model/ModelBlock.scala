package dev.tlang.tlang.ast.model

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent, DomainBlock}

case class ModelBlock(context: Null, content: Option[List[ModelContent[_]]]) extends DomainBlock with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(getClass)
}
