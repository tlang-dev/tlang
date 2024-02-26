package dev.tlang.tlang.ast.model.set

import tlang.core.{Null, Type}
import tlang.internal.{AstContext, ClassType, ContextContent}

case class ModelSetRefCurrying(context: Null, values: List[ModelSetRefValue]) extends AstContext {
  override def getContext: Null = context

  override def getType: Type = ClassType.of(this.getClass)
}
