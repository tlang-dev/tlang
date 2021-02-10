package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock
import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock

case class TmplIf(cond: TmplConditionBlock, content: TmplExprContent, elseBlock: Option[Either[TmplExprContent, TmplIf]]) extends TmplExpression
