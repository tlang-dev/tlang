package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl.TmplValueType
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplConditionBlock(context: Option[ContextContent], content: Either[TmplConditionBlock, TmplCondition], link: Option[Operator.operator] = None, nextBlock: Option[TmplConditionBlock] = None) extends TmplValueType with AstContext {
  override def deepCopy(): TmplConditionBlock = TmplConditionBlock(context,
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy())
    },
    link,
    if (nextBlock.isDefined) Some(nextBlock.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context
}
