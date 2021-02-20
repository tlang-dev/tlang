package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}

case class TmplDoWhile(content: TmplExprContent, cond: TmplConditionBlock) extends TmplExpression {
  override def deepCopy(): TmplDoWhile =
    TmplDoWhile(content.deepCopy().asInstanceOf[TmplExprContent], cond.deepCopy().asInstanceOf[TmplConditionBlock])
}
