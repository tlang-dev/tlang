package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class CallVarObject(context: Null[ContextContent], name: String) extends CallObjectType with AstContext {
  override def getContext: Null[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
