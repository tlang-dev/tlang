package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplLongValue(context: Option[ContextContent], value: Long) extends TmplPrimitiveValue[TmplLongValue] with AstContext {
  override def deepCopy(): TmplLongValue = TmplLongValue(context, value)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplLongValue]): Int = this.value.compareTo(value.getElement.value)

  override def getElement: TmplLongValue = this

  override def getType: String = getClass.getName
}
