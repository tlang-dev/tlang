package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplInclude(context: Option[ContextContent], calls: List[CallObject]) extends TmplExpression[TmplInclude] {
  override def deepCopy(): TmplInclude = TmplInclude(context, calls)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplInclude]): Int = 0

  override def getElement: TmplInclude = this

  override def getType: String = getClass.getName
}
