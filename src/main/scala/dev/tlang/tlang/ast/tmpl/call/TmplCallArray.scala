package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplCallArray(context: Option[ContextContent], var name: TmplID, var elem: TmplOperation) extends TmplCallObjType[TmplCallArray] {
  override def deepCopy(): TmplCallArray = TmplCallArray(context, name.deepCopy().asInstanceOf[TmplID], elem.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallArray]): Int = 0

  override def getElement: TmplCallArray = this

  override def getType: String = getClass.getName
}
