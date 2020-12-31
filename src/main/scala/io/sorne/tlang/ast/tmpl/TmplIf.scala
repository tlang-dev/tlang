package io.sorne.tlang.ast.tmpl

import io.sorne.tlang.ast.tmpl.condition.TmplConditionBlock

case class TmplIf(cond: TmplConditionBlock, content: TmplExprContent, elseBlock: Option[Either[TmplExprContent, TmplIf]]) extends TmplExpression
