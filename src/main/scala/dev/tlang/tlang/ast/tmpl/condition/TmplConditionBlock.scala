package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.helper.ConditionLink
import dev.tlang.tlang.ast.tmpl.TmplValueType

case class TmplConditionBlock(content: Either[TmplConditionBlock, TmplCondition], link: Option[ConditionLink.condition] = None, nextBlock: Option[TmplConditionBlock] = None) extends TmplValueType
