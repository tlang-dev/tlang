package io.sorne.tlang.ast.common.condition

import io.sorne.tlang.ast.common.call.SimpleValueStatement
import io.sorne.tlang.ast.helper.{ConditionLink, ConditionType}

case class Condition(statement1: SimpleValueStatement[_], condition: Option[ConditionType.condition] = None, statement2: Option[SimpleValueStatement[_]] = None,
                     link: Option[ConditionLink.condition] = None, nextBlock: Option[ConditionBlock] = None)
