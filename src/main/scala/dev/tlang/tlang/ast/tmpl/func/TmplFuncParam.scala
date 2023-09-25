package dev.tlang.tlang.ast.tmpl.func

import dev.tlang.tlang.ast.tmpl.{TmplNode, TmplParam}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplFuncParam(context: Option[ContextContent], params: Option[List[TmplParam]], var `type`: String) extends TmplNode[TmplFuncParam] {
  override def compareTo(value: Value[TmplFuncParam]): Int = 0

  override def getElement: TmplFuncParam = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplFuncParam = TmplFuncParam(context,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    `type`
  )
}
