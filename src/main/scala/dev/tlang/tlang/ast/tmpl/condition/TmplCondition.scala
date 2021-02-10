package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}
import dev.tlang.tlang.ast.tmpl.TmplSimpleValueType

case class TmplCondition(statement1: TmplSimpleValueType, condition: Option[ConditionType.condition] = None, statement2: Option[TmplSimpleValueType] = None,
                         link: Option[ConditionLink.condition] = None, nextBlock: Option[TmplConditionBlock] = None)
