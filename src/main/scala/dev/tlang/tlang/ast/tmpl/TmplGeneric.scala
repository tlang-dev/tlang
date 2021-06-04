package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplGeneric(context: Option[ContextContent], var types: List[TmplType]) extends TmplNode[TmplGeneric] {
  override def deepCopy(): TmplGeneric = TmplGeneric(context, types.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplGeneric]): Int = 0

  override def getElement: TmplGeneric = this

  override def getType: String = getClass.getName
}
