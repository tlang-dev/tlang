package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplGeneric(context: Option[ContextContent], var types: List[TmplType]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplGeneric = TmplGeneric(context, types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context
}
