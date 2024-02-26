package dev.tlang.tlang.ast

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class DomainExpose(context: Null, name: String) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(getClass)
}
