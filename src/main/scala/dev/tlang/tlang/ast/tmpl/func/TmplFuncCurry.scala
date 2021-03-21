package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplParam}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplFuncCurry(context: Option[ContextContent], var params: Option[List[TmplParam]]) extends DeepCopy with AstContext {
  override def deepCopy(): TmplFuncCurry = TmplFuncCurry(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
