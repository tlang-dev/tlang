package dev.tlang.tlang.ast.model.set

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class ModelSetRefCurrying(context: Option[ContextContent], values: List[ModelSetRefValue]) extends AstContext {
  override def getContext: Option[ContextContent] = context
}
