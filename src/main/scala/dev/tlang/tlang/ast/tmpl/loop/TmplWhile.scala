package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplWhile(context: Option[ContextContent], cond: TmplConditionBlock, content: TmplExprContent) extends TmplExpression with AstContext {
  override def deepCopy(): TmplWhile =
    TmplWhile(context, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent])

  override def getContext: Option[ContextContent] = context
}
