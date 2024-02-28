package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class DomainExpose(context: Option[ContextContent], name: String) extends Context {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
