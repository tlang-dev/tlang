package dev.tlang.tlang.tmpl

import tlang.core.Type
import tlang.internal.ContextContent

case class AstAnyValue(context: Option[ContextContent], `type`: Type) extends AstValue {
  override def getType: Type = `type`

  override def getElement: AstValue = this

  override def getContext: Option[ContextContent] = context
}
