package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplExprBlock(context: Option[ContextContent], exprs: List[TmplExpression[_]]) extends TmplExprContent[TmplExprBlock] with AstContext {
  override def deepCopy(): TmplExprBlock = TmplExprBlock(context, exprs.map(_.deepCopy().asInstanceOf[TmplExpression[_]]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplExprBlock]): Int = 0

  override def getElement: TmplExprBlock = this

  override def getType: String = getClass.getName
}
