package dev.tlang.tlang.ast.helper

import dev.tlang.tlang.ast.common.condition.ConditionBlock

case class HelperIf(condition: ConditionBlock, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement
