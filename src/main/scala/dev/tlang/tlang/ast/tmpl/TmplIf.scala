package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplConditionBlock

case class TmplIf(cond: TmplConditionBlock, content: TmplExprContent, elseBlock: Option[Either[TmplExprContent, TmplIf]]) extends TmplExpression {
  override def deepCopy(): TmplIf = TmplIf(cond.deepCopy().asInstanceOf[TmplConditionBlock], content.deepCopy().asInstanceOf[TmplExprContent],
    if (elseBlock.isDefined) elseBlock.get match {
      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[TmplExprContent]))
      case Right(value) => Some(Right(value.deepCopy()))
    } else None,
  )
}
