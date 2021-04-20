package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.{TmplExprBlock, TmplExpression}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplFor(context: Option[ContextContent], content: TmplExprBlock) extends TmplExpression[TmplFor] {
  override def deepCopy(): TmplFor = TmplFor(context, content.deepCopy())

  override def compareTo(value: Value[TmplFor]): Int = 0

  override def getElement: TmplFor = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}
