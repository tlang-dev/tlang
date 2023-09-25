package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplNode
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCallFuncParam(context: Option[ContextContent], var params: Option[List[TmplNode[_]]]) extends TmplNode[TmplCallFuncParam] {
  override def compareTo(value: Value[TmplCallFuncParam]): Int = 0

  override def getElement: TmplCallFuncParam = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplCallFuncParam = TmplCallFuncParam(
    context,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None,
  )
}
