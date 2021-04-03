package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplExpression}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}

case class TmplOperation(context: Option[ContextContent], var content: Either[TmplOperation, TmplExpression], var next: Option[(Operator.operator, TmplOperation)] = None) extends DeepCopy with AstContext {
  override def deepCopy(): TmplOperation = TmplOperation(context,
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy().asInstanceOf[TmplExpression])
    },
    if (next.isDefined) Some((next.get._1, next.get._2.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context
}
