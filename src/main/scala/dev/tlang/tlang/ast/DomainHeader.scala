package dev.tlang.tlang.ast

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class DomainHeader(context: Null[ContextContent], exposes: Option[List[DomainExpose]], uses: Option[List[DomainUse]]) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
