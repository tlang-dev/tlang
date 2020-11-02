package io.sorne.tlang.ast.helper

case class HelperConditionBlock(content: HelperCondition, link: Option[ConditionLink.condition] = None, nextBlock: Option[HelperConditionBlock]=None) extends HelperStatement
