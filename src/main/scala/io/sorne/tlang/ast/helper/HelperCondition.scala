package io.sorne.tlang.ast.helper

import io.sorne.tlang.ast.helper.call.HelperCallObject

case class HelperCondition(statement1: HelperCallObject, condition: Option[ConditionType.condition] = None, statement2: Option[HelperCallObject] = None,
                           link: Option[ConditionLink.condition] = None, nextBlock: Option[HelperConditionBlock] = None)
