package dev.tlang.tlang.ast

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class DomainExpose(context: Null[ContextContent], name: String) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
