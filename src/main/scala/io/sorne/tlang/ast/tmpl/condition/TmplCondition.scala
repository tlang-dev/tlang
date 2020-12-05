package io.sorne.tlang.ast.tmpl.condition

import io.sorne.tlang.ast.helper.{ConditionLink, ConditionType}
import io.sorne.tlang.ast.tmpl.TmplSimpleValueType

case class TmplCondition(statement1: TmplSimpleValueType, condition: Option[ConditionType.condition] = None, statement2: Option[TmplSimpleValueType] = None,
                         link: Option[ConditionLink.condition] = None, nextBlock: Option[TmplConditionBlock] = None)
