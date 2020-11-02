package io.sorne.tlang.ast.helper

case class HelperCondition(statement1: HelperStatement, condition: Option[ConditionType.condition] = None, statement2: Option[HelperStatement] = None,
                           link: Option[ConditionLink.condition] = None, nextCondition: Option[HelperCondition] = None)
