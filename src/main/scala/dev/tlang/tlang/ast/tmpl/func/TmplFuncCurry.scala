package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplNode, TmplParam}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplFuncCurry(context: Option[ContextContent], var params: Option[List[TmplParam]]) extends DeepCopy with AstContext with TmplNode[TmplFuncCurry] {
  override def deepCopy(): TmplFuncCurry = TmplFuncCurry(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplFuncCurry]): Int = 0

  override def getElement: TmplFuncCurry = this

  override def getType: String = getClass.getName
}
