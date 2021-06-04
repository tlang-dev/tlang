package dev.tlang.tlang.ast.tmpl.loop

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.{TmplExprContent, TmplExpression, TmplID}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplFor(context: Option[ContextContent], var variable: TmplID, var start: Option[TmplOperation], forType: ForType.ForType, var cond: TmplOperation, var content: TmplExprContent[_]) extends TmplExpression[TmplFor] {
  override def deepCopy(): TmplFor = TmplFor(context,
    variable.deepCopy().asInstanceOf[TmplID],
    if (start.isDefined) Some(start.get.deepCopy()) else None,
    forType, cond.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def compareTo(value: Value[TmplFor]): Int = 0

  override def getElement: TmplFor = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}

object ForType extends Enumeration {
  type ForType = Value
  val IN, TO, UNTIL = Value
}