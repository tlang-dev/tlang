package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class DomainExpose(context: Null[ContextContent], name: String) extends AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(getClass)
}
