package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class CallFuncObject(context: Null, name: Option[String], currying: Option[List[CallFuncParam]]) extends CallObjectType with AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
