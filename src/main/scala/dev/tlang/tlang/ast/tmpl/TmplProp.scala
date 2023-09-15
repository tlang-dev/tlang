package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplProp(context: Option[ContextContent], var props: List[TmplID]) extends DeepCopy with AstContext with TmplNode[TmplProp] {
  override def deepCopy(): TmplProp = TmplProp(context, props.map(_.deepCopy().asInstanceOf[TmplID]))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplProp]): Int = 0

  override def getElement: TmplProp = this

  override def getType: String = getClass.getName
}
