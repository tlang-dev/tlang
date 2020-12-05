package io.sorne.tlang.ast.tmpl.condition

import io.sorne.tlang.ast.helper.ConditionLink
import io.sorne.tlang.ast.tmpl.TmplValueType

case class TmplConditionBlock(content: Either[TmplConditionBlock, TmplCondition], link: Option[ConditionLink.condition] = None, nextBlock: Option[TmplConditionBlock] = None) extends TmplValueType
