package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplSimpleValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplCondition(context: Option[ContextContent], statement1: TmplSimpleValueType, condition: Option[Operator.operator] = None, statement2: Option[TmplSimpleValueType] = None,
                         link: Option[Operator.operator] = None, nextBlock: Option[TmplConditionBlock] = None) extends DeepCopy with AstContext {
  override def deepCopy(): TmplCondition = TmplCondition(context,
    statement1.deepCopy().asInstanceOf[TmplSimpleValueType],
    condition,
    if (statement2.isDefined) Some(statement2.get.deepCopy().asInstanceOf[TmplSimpleValueType]) else None,
    link,
    if (nextBlock.isDefined) Some(nextBlock.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context
}
