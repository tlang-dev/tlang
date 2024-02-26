package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal
import tlang.internal.{ClassType, ContextContent}

case class DomainUse(context: Null, parts: List[String], alias: Option[String] = None) extends internal.AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
