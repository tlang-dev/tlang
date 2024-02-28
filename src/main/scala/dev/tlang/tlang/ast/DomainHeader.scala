package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class DomainHeader(context: Option[ContextContent], exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]]) extends Context {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
