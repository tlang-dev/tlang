package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplSetAttribute}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCurryParam(context: Option[ContextContent], var params: Option[List[TmplSetAttribute]]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplCurryParam = TmplCurryParam(
    context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
