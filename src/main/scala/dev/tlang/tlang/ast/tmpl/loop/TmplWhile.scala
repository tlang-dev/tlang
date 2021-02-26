package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}

case class TmplWhile(cond: TmplConditionBlock, content: TmplExprContent) extends TmplExpression {
  override def deepCopy(): TmplWhile =
    TmplWhile(cond.deepCopy().asInstanceOf[TmplConditionBlock], content.deepCopy().asInstanceOf[TmplExprContent])
}
