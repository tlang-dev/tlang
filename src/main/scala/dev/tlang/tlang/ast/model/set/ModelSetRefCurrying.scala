package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{Context, ClassType, ContextContent}

case class ModelSetRefCurrying(context: Null, values: List[ModelSetRefValue]) extends Context {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
