package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplStringValue(context: Option[ContextContent], var value: TmplID) extends TmplPrimitiveValue with AstContext {
  override def deepCopy(): TmplStringValue = TmplStringValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context
}
