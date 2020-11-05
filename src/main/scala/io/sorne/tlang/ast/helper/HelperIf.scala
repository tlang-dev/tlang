package io.sorne.tlang.ast.helper

case class HelperIf(condition: HelperConditionBlock, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement
