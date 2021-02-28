package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplDoWhile(context: Option[ContextContent], content: TmplExprContent, cond: TmplConditionBlock) extends TmplExpression with AstContext {
  override def deepCopy(): TmplDoWhile =
    TmplDoWhile(context, content.deepCopy().asInstanceOf[TmplExprContent], cond.deepCopy())

  override def getContext: Option[ContextContent] = context
}
