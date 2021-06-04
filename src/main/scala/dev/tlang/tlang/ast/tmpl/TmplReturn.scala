package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplReturn(context: Option[ContextContent], var operation: TmplOperation) extends TmplExpression[TmplReturn] with AstContext {
  override def deepCopy(): TmplReturn = TmplReturn(context, operation.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplReturn]): Int = 0

  override def getElement: TmplReturn = this

  override def getType: String = getClass.getName
}
