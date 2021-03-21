package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplLongValue(context: Option[ContextContent], value: Long) extends TmplPrimitiveValue with AstContext {
  override def deepCopy(): TmplLongValue = TmplLongValue(context, value)

  override def getContext: Option[ContextContent] = context
}
