package dev.tlang.tlang.ast.tmpl.loop

import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock
import io.sorne.tlang.ast.tmpl.{TmplExprContent, TmplExpression}

case class TmplWhile(cond: TmplConditionBlock, content: TmplExprContent) extends TmplExpression
