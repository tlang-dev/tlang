package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{ClassType, ContextContent}
import tlang.{core, internal}

case class DomainUse(context: Null[internal.ContextContent], parts: List[String], alias: Null[core.String] = Null.empty()) extends internal.AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
