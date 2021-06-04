package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCallVar(context: Option[ContextContent], var name: TmplID) extends TmplCallObjType[TmplCallVar] {
  override def deepCopy(): TmplCallVar = TmplCallVar(context, name.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallVar]): Int = 0

  override def getElement: TmplCallVar = this

  override def getType: String = getClass.getName
}
