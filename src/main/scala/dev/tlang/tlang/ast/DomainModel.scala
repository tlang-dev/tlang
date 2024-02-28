package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent, DomainBlock}

case class DomainModel(context: Option[ContextContent], header: Option[DomainHeader], body: List[DomainBlock]) extends Context {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
