package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCallVar(context: Option[ContextContent], var name: TmplID) extends TmplCallObjType with AstContext {
  override def deepCopy(): TmplCallVar = TmplCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context
}
