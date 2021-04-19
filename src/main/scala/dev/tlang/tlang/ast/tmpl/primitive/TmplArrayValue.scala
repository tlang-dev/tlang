package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplSetAttribute, TmplType}
import dev.tlang.tlang.astbuilder.context.{AstContext, ContextContent}
import dev.tlang.tlang.interpreter.Value

case class TmplArrayValue(context: Option[ContextContent], `type`: Option[TmplType] = None, params: Option[List[TmplSetAttribute]]) extends TmplPrimitiveValue[TmplArrayValue] with AstContext {
  override def deepCopy(): TmplArrayValue = TmplArrayValue(context,
    if (`type`.isDefined) Some(`type`.get.deepCopy()) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[TmplArrayValue]): Int = 0

  override def getElement: TmplArrayValue = this

  override def getType: String = getClass.getName
}
