package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.func.TmplFuncCurry
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplSpecialBlock(context: Option[ContextContent], var `type`: String, var curries: Option[List[TmplFuncCurry]], var content: Option[TmplExprContent[_]]) extends TmplExpression[TmplSpecialBlock] with TmplContent[TmplSpecialBlock] with AstContext {
  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplSpecialBlock]): Int = 0

  override def deepCopy(): TmplSpecialBlock = TmplSpecialBlock(
    context,
    `type` = `type`,
    if (curries.isDefined) Some(curries.get.map(_.deepCopy())) else None,
    content = if (content.isDefined) Some(content.get.deepCopy().asInstanceOf[TmplExprContent[_]]) else None)

  override def getElement: TmplSpecialBlock = this

  override def getType: String = getClass.getName
}
