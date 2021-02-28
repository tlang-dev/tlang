package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplTextValue(context: Option[ContextContent], var value: TmplID) extends TmplPrimitiveValue with AstContext {
  override def deepCopy(): TmplTextValue = TmplTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context
}
