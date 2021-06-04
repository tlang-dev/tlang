package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplNode
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCurryParam(context: Option[ContextContent], var params: Option[List[TmplNode[_]]]) extends TmplNode[TmplCurryParam] {
  override def deepCopy(): TmplCurryParam = TmplCurryParam(
    context,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCurryParam]): Int = 0

  override def getElement: TmplCurryParam = this

  override def getType: String = getClass.getName
}
