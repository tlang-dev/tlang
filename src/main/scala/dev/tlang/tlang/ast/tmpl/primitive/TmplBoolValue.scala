package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplBoolValue(context: Option[ContextContent], value: Boolean) extends TmplPrimitiveValue with AstContext {
  override def deepCopy(): TmplBoolValue = TmplBoolValue(context, if (value) true else false)

  override def getContext: Option[ContextContent] = context
}
