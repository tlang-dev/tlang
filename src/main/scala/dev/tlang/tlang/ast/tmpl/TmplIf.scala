package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplIf(context: Option[ContextContent], cond: TmplOperation, content: TmplExprContent, elseBlock: Option[Either[TmplExprContent, TmplIf]]) extends TmplExpression with AstContext {
  override def deepCopy(): TmplIf = TmplIf(context, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent],
    if (elseBlock.isDefined) elseBlock.get match {
      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[TmplExprContent]))
      case Right(value) => Some(Right(value.deepCopy()))
    } else None,
  )

  override def getContext: Option[ContextContent] = context
}
