package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{TmplID, TmplValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCallArray(context: Option[ContextContent], var name: TmplID, var elem: TmplValueType) extends TmplCallObjType with AstContext {
  override def deepCopy(): TmplCallArray = TmplCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy().asInstanceOf[TmplValueType])

  override def getContext: Option[ContextContent] = context
}
