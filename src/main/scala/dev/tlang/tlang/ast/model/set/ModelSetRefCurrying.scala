package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.tmpl.AstContext
import tlang.core.Type
import tlang.internal.{ClassType, ContextContent}

case class ModelSetRefCurrying(context: Option[ContextContent], values: List[ModelSetRefValue]) extends AstContext {
  override def getContext: Option[ContextContent] = context

  override def getType: Type = ClassType.of(this.getClass)
}
