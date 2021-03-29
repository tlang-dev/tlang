package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallObj
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplReturn(context: Option[ContextContent], var call: TmplCallObj) extends TmplExpression with AstContext {
  override def deepCopy(): TmplReturn = TmplReturn(context, call.deepCopy())

  override def getContext: Option[ContextContent] = context
}
