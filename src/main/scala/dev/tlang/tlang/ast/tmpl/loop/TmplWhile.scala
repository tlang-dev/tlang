package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplWhile(context: Option[ContextContent], cond: TmplOperation, content: TmplExprContent[_]) extends TmplExpression[TmplWhile] with AstContext {
  override def deepCopy(): TmplWhile =
    TmplWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplWhile]): Int = 0

  override def getElement: TmplWhile = this

  override def getType: String = getClass.getName
}
