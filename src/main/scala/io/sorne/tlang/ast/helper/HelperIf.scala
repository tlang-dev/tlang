package io.sorne.tlang.ast.helper

case class HelperIf(statement1: HelperStatement, condition: Option[Condition.condition], statement2: Option[HelperStatement]) extends HelperStatement
