package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class CallFuncParam(context: Null, params: Option[List[SetAttribute]]) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
