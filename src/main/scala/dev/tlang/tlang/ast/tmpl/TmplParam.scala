package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplParam(context: Option[ContextContent], var name: TmplID, var `type`: TmplType) extends TmplNode[TmplParam] {
  override def deepCopy(): TmplParam = TmplParam(context, name.deepCopy().asInstanceOf[TmplID], `type`.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplParam]): Int = 0

  override def getElement: TmplParam = this

  override def getType: String = getClass.getName
}
