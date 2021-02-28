package dev.tlang.tlang.ast.common.condition

import dev.tlang.tlang.ast.common.call.ComplexValueStatement
import dev.tlang.tlang.ast.common.value.TLangType
import dev.tlang.tlang.ast.helper.ConditionLink
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class ConditionBlock(context: Option[ContextContent],content: Either[ConditionBlock, Condition], link: Option[ConditionLink.condition] = None, nextBlock: Option[ConditionBlock] = None) extends ComplexValueStatement[ConditionBlock] with AstContext{
  override def getValue: ConditionBlock = this

  override def getType: String = ConditionBlock.getType

  override def compareTo(value: Value[ConditionBlock]): Int = 0

  override def getContext: Option[ContextContent] = context
}

object ConditionBlock extends TLangType {
  override def getType: String = getClass.getName

}
