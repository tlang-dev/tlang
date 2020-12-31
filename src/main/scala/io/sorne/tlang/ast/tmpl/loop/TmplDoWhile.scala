package io.sorne.tlang.ast.tmpl.loop

import io.sorne.tlang.ast.tmpl.{TmplExprContent, TmplExpression}
import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock

case class TmplDoWhile(content: TmplExprContent, cond: TmplConditionBlock) extends TmplExpression
