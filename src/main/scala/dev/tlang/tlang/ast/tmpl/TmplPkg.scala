package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplPkg(context: Option[ContextContent], var parts: List[TmplID]) extends DeepCopy with TmplNode[TmplPkg] {
  override def deepCopy(): TmplPkg = {
    TmplPkg(context, parts.map(_.deepCopy().asInstanceOf[TmplID]))
  }

  override def compareTo(value: Value[TmplPkg]): Int = 0

  override def getElement: TmplPkg = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}
