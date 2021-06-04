package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplIf(context: Option[ContextContent], cond: TmplOperation, content: TmplExprContent[_], elseBlock: Option[Either[TmplExprContent[_], TmplIf]]) extends TmplExpression[TmplIf] with AstContext {
  override def deepCopy(): TmplIf = TmplIf(context, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]],
    if (elseBlock.isDefined) elseBlock.get match {
      case Left(value) => Some(Left(value.deepCopy().asInstanceOf[TmplExprContent[_]]))
      case Right(value) => Some(Right(value.deepCopy()))
    } else None,
  )

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplIf]): Int = 0

  override def getElement: TmplIf = this

  override def getType: String = getClass.getName
}
