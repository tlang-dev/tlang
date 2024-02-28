package dev.tlang.tlang.tmpl

import tlang.core.{Array, Type}
import tlang.internal.{ClassType, ContextContent}

case class AstListValue(context: Option[ContextContent], values:List[AstValue]) extends AstValue {

  override def getElement: AstValue = this

  override def getContext: Option[ContextContent] = context

  override def getType: Type = Array.TYPE
}
