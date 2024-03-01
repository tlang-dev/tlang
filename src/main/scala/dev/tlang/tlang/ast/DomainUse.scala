package dev.tlang.tlang.ast

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class DomainUse(context: Option[ContextContent], parts: List[String], alias: Option[String] = None) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
