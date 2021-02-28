package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplDoubleValue(context: Option[ContextContent], value: Double) extends TmplPrimitiveValue with AstContext {
  override def deepCopy(): TmplDoubleValue = TmplDoubleValue(context, value)

  override def getContext: Option[ContextContent] = context
}
