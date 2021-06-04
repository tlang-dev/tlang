package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.func.TmplFuncCurry
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplAnonFunc(context: Option[ContextContent], var currying: TmplFuncCurry, var content: TmplExprContent[_]) extends TmplExpression[TmplAnonFunc] {
  override def getElement: TmplAnonFunc = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplAnonFunc = TmplAnonFunc(context, currying.deepCopy(), content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def compareTo(value: Value[TmplAnonFunc]): Int = 0
}
