package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock

case class TmplDoWhile(content: TmplExprContent, cond: TmplConditionBlock) extends TmplExpression
