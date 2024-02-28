package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal
import tlang.internal.{ClassType, Context, ContextContent}

case class DomainUse(context: Option[ContextContent], parts: List[String], alias: Option[String] = None) extends Context {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
