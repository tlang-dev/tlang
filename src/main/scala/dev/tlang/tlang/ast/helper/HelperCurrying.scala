package dev.tlang.tlang.ast.helper

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class HelperCurrying(context: Null, params: List[HelperParam]) extends Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
