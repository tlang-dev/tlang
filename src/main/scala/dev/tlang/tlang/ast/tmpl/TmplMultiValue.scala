package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplMultiValue(context: Option[ContextContent], var values: List[TmplValueType]) extends TmplValueType with AstContext {
  override def deepCopy(): TmplMultiValue = TmplMultiValue(context, values.map(_.deepCopy().asInstanceOf[TmplValueType]))

  override def getContext: Option[ContextContent] = context
}
