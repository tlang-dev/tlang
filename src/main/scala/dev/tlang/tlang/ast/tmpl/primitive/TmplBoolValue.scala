package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplBoolValue(context: Option[ContextContent], value: Boolean) extends TmplPrimitiveValue[TmplBoolValue] with AstContext {
  override def deepCopy(): TmplBoolValue = TmplBoolValue(context, if (value) true else false)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplBoolValue]): Int = 0

  override def getElement: TmplBoolValue = this

  override def getType: String = getClass.getName
}
