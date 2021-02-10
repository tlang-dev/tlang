package dev.tlang.tlang.ast.common.condition

import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}
import dev.tlang.tlang.ast.common.call.SimpleValueStatement
import dev.tlang.tlang.ast.helper.{ConditionLink, ConditionType}

case class Condition(statement1: SimpleValueStatement[_], condition: Option[ConditionType.condition] = None, statement2: Option[SimpleValueStatement[_]] = None,
                     link: Option[ConditionLink.condition] = None, nextBlock: Option[ConditionBlock] = None)
