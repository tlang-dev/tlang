package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplMultiValue(context: Option[ContextContent], var values: List[TmplValueType[_]]) extends TmplValueType[TmplMultiValue] with AstContext {
  override def deepCopy(): TmplMultiValue = TmplMultiValue(context, values.map(_.deepCopy().asInstanceOf[TmplValueType[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplMultiValue]): Int = 0

  override def getElement: TmplMultiValue = this

  override def getType: String = getClass.getName
}
