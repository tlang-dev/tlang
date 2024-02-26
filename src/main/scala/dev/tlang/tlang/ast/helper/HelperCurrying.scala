package dev.tlang.tlang.ast.helper

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class HelperCurrying(context: Null, params: List[HelperParam]) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
