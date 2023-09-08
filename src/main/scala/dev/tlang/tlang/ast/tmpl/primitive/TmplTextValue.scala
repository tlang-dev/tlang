package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.TmplID
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplTextValue(context: Option[ContextContent], var value: TmplID) extends TmplPrimitiveValue[TmplTextValue] with AstContext {
  override def deepCopy(): TmplTextValue = TmplTextValue(context, value.deepCopy().asInstanceOf[TmplID])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplTextValue]): Int = this.value.toString.compareTo(value.toString)

  override def getElement: TmplTextValue = this

  override def getType: String = getClass.getName

  override def toString: String = value.toString
}
