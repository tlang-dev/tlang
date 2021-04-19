package dev.tlang.tlang.ast.tmpl.primitive

import dev.tlang.tlang.ast.tmpl.{TmplAttribute, TmplID}
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class TmplEntityValue(context: Option[ContextContent], name: Option[TmplID], params: Option[List[TmplAttribute]], attrs: Option[List[TmplAttribute]]) extends TmplPrimitiveValue[TmplEntityValue] {
  override def deepCopy(): TmplEntityValue = TmplEntityValue(context,
    if (name.isDefined) Some(name.get.deepCopy().asInstanceOf[TmplID]) else None,
    if (params.isDefined) Some(params.get.map(_.deepCopy())) else None,
    if (attrs.isDefined) Some(attrs.get.map(_.deepCopy())) else None
  )

  override def compareTo(value: Value[TmplEntityValue]): Int = 0

  override def getElement: TmplEntityValue = this

  override def getType: String = getClass.getName

  override def getContext: Option[ContextContent] = context
}
