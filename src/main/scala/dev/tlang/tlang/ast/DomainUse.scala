package dev.tlang.tlang.ast

import tlang.core.Null
import tlang.internal.ContextContent
import tlang.{core, internal}

case class DomainUse(context: Null[internal.ContextContent], parts: List[String], alias: Null[core.String] = Null.empty()) extends internal.AstContext {
  override def getContext: Null[ContextContent] = context
}
