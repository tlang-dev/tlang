package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplDoWhile(context: Option[ContextContent], content: TmplExprContent[_], cond: TmplOperation) extends TmplExpression[TmplDoWhile] with AstContext {
  override def deepCopy(): TmplDoWhile =
    TmplDoWhile(context, content.deepCopy().asInstanceOf[TmplExprContent[_]], cond.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplDoWhile]): Int = 0

  override def getElement: TmplDoWhile = this

  override def getType: String = getClass.getName
}
