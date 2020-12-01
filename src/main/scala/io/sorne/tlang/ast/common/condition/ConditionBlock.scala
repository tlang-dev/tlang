package io.sorne.tlang.ast.common.condition

import io.sorne.tlang.ast.common.call.ComplexValueStatement
import io.sorne.tlang.ast.common.value.TLangType
import io.sorne.tlang.ast.helper.ConditionLink
import io.sorne.tlang.interpreter.Value

case class ConditionBlock(content: Either[ConditionBlock, Condition], link: Option[ConditionLink.condition] = None, nextBlock: Option[ConditionBlock] = None) extends ComplexValueStatement[ConditionBlock] {
  override def getValue: ConditionBlock = this

  override def getType: String = ConditionBlock.getType

  override def compareTo(value: Value[ConditionBlock]): Int = 0
}

object ConditionBlock extends TLangType {
  override def getType: String = getClass.getName
}
