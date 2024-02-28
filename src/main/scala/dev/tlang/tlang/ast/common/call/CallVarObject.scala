package dev.tlang.tlang.ast.common.call

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class CallVarObject(context: Null, name: String) extends CallObjectType with Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
