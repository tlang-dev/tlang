package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.call.TmplCallObj
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplAffect(context: Option[ContextContent], var variable: TmplCallObj, var value: TmplOperation) extends TmplExpression[TmplAffect] with AstContext {
  override def deepCopy(): TmplAffect = TmplAffect(context, variable.deepCopy(), value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplAffect]): Int = 0

  override def getElement: TmplAffect = this

  override def getType: String = getClass.getName
}
