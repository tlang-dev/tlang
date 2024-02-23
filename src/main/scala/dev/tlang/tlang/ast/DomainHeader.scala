package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class DomainHeader(context: Null[ContextContent], exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]]) extends AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
