package dev.tlang.tlang.tmpl

import tlang.core.Type
import tlang.internal.ContextContent

case class AstModel(context: Option[ContextContent], name: Type, ext: Option[AstModel], params: Option[List[AstEntityAttr]], attrs: Option[List[AstEntityAttr]]) extends AstValue {

  override def getElement: AstValue = this

  override def getContext: Option[ContextContent] = context

  override def getType: Type = name
}
