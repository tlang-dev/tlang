package dev.tlang.tlang.ast

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent, DomainBlock}

case class DomainModel(context: Option[ContextContent], header: Option[DomainHeader], body: List[DomainBlock]) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
