package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}

case class TmplWhile(cond: TmplConditionBlock, content: TmplExprContent) extends TmplExpression
