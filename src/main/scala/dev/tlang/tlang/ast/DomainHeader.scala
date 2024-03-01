package dev.tlang.tlang.ast

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class DomainHeader(context: Option[ContextContent], exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]]) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
