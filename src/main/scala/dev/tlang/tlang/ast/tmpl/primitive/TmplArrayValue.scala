package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplNode, TmplType}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplArrayValue(context: Option[ContextContent], var `type`: Option[TmplType] = None, var params: Option[List[TmplNode[_]]]) extends TmplPrimitiveValue[TmplArrayValue] {
  override def deepCopy(): TmplArrayValue = TmplArrayValue(context,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy().asInstanceOf[TmplNode[_]])) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplArrayValue]): Int = 0

  override def getElement: TmplArrayValue = this

  override def getType: String = getClass.getName
}
