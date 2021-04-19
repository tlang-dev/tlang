package dev.tlang.tlang.ast.tmpl.condition

import dev.tlang.tlang.ast.common.operation.Operator
import dev.tlang.tlang.ast.tmpl.{DeepCopy, TmplExpression, TmplNode}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplOperation(context: Option[ContextContent], var content: Either[TmplOperation, TmplExpression[_]], var next: Option[(Operator.operator, TmplOperation)] = None) extends DeepCopy with TmplNode[TmplOperation] {
  override def deepCopy(): TmplOperation = TmplOperation(context,
    content match {
      case Left(value) => Left(value.deepCopy())
      case Right(value) => Right(value.deepCopy().asInstanceOf[TmplExpression[_]])
    },
    if (next.isDefined) Some((next.get._1, next.get._2.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplOperation]): Int = 0

  override def getElement: TmplOperation = this

  override def getType: String = getClass.getName
}
