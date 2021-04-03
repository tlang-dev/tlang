package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCallArray(context: Option[ContextContent], var name: TmplID, var elem: TmplOperation) extends TmplCallObjType with AstContext {
  override def deepCopy(): TmplCallArray = TmplCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getContext: Option[ContextContent] = context
}
