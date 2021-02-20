package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.helper.ConditionLink
import dev.tlang.tlang.ast.tmpl.TmplValueType

case class TmplConditionBlock(content: Either[TmplConditionBlock, TmplCondition], link: Option[ConditionLink.condition] = None, nextBlock: Option[TmplConditionBlock] = None) extends TmplValueType {
  override def deepCopy(): TmplConditionBlock = TmplConditionBlock(
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy())
    },
    link,
    if (nextBlock.isDefined) Some(nextBlock.get.deepCopy()) else None)
}
