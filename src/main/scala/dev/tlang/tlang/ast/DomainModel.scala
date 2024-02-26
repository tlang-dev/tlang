package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent, DomainBlock}

case class DomainModel(context: Null, header: Option[DomainHeader], body: List[DomainBlock]) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
