package dev.tlang.tlang.ast.tmpl

import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplSetAttribute(context: Option[ContextContent], var name: Option[TmplID], var value: TmplOperation) extends TmplNode[TmplSetAttribute] {
  override def deepCopy(): TmplSetAttribute = TmplSetAttribute(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None, value.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplSetAttribute]): Int = 0

  override def getElement: TmplSetAttribute = this

  override def getType: String = getClass.getName
}
