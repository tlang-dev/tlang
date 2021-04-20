package dev.tlang.tlang.ast.tmpl.call

import dev.tlang.tlang.ast.tmpl.{TmplExpression, TmplSimpleValueType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplCallObj(context: Option[ContextContent], var calls: List[TmplCallObjType]) extends TmplSimpleValueType[TmplCallObj] with TmplExpression[TmplCallObj] with AstContext {
  override def deepCopy(): TmplCallObj = TmplCallObj(context, calls.map(_.deepCopy().asInstanceOf[TmplCallObjType]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplCallObj]): Int = 0

  override def getElement: TmplCallObj = this

  override def getType: String = getClass.getName
}
