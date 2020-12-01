package io.sorne.tlang.ast.helper

import io.sorne.tlang.ast.common.condition.ConditionBlock

case class HelperIf(condition: ConditionBlock, ifTrue: Option[HelperContent] = None, ifFalse: Option[HelperContent] = None) extends HelperStatement
