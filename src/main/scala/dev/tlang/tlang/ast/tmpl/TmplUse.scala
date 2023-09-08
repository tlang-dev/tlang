package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplUse(context: Option[ContextContent], var parts: List[TmplID], var alias: Option[TmplID] = None) extends TmplNode[TmplUse] with DeepCopy {
  override def deepCopy(): TmplUse = TmplUse(context, parts.map(_.deepCopy().asInstanceOf[TmplID]),
    if (alias.isDefined) Some(alias.get.deepCopy().asInstanceOf[TmplID]) else None)

  override def compareTo(value: Value[TmplUse]): Int = 0

  override def getElement: TmplUse = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}
