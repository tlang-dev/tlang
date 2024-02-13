package dev.tlang.tlang.ast.model.set

import tlang.core.Null
import tlang.internal.{AstContext, ContextContent}

case class ModelSetRefCurrying(context: Null[ContextContent], values: List[ModelSetRefValue]) extends AstContext {
  override def getContext: Null[ContextContent] = context
}
