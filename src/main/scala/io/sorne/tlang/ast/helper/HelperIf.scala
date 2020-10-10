package io.sorne.tlang.ast.helper

case class HelperIf(statement1: HelperStatement, condition: Option[Condition.condition] = None, statement2: Option[HelperStatement] = None, ifTrue: Option[HelperBlock] = None, ifFalse: Option[HelperBlock] = None) extends HelperStatement
