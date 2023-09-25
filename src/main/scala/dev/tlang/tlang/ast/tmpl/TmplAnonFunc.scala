package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.func.TmplFuncParam
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplAnonFunc(context: Option[ContextContent], var curries: Option[List[TmplFuncParam]], var content: TmplExprContent[_]) extends TmplExpression[TmplAnonFunc] {
  override def getElement: TmplAnonFunc = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): TmplAnonFunc = TmplAnonFunc(context,
    if (curries.isDefined) Some(curries.get.map(curry => curry.deepCopy())) else None,
    content.deepCopy().asInstanceOf[TmplExprContent[_]])

  override def compareTo(value: Value[TmplAnonFunc]): Int = 0
}
