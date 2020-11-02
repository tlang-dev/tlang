package io.sorne.tlang.ast.helper

case class HelperIf(condition: HelperConditionBlock, ifTrue: Option[HelperBlock] = None, ifFalse: Option[HelperBlock] = None) extends HelperStatement
