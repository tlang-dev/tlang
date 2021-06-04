package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplDoubleValue(context: Option[ContextContent], value: Double) extends TmplPrimitiveValue[TmplDoubleValue] with AstContext {
  override def deepCopy(): TmplDoubleValue = TmplDoubleValue(context, value)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplDoubleValue]): Int = 0

  override def getElement: TmplDoubleValue = this

  override def getType: String = getClass.getName
}
