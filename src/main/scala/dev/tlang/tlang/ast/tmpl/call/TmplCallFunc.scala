package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCallFunc(context: Option[ContextContent], var name: TmplID, var currying: Option[List[TmplCurryParam]]) extends TmplCallObjType with AstContext {
  override def deepCopy(): TmplCallFunc = TmplCallFunc(context, name.deepCopy().asInstanceOf[TmplID],
    if (currying.isDefined) Some(currying.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
