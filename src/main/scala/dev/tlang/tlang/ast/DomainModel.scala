package dev.tlang.tlang.ast

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class DomainModel(context: Null[ContextContent], header: Option[DomainHeader], body: List[DomainBlock]) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
