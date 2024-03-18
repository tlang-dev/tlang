package dev.tlang.tlang.tmpl

import tlang.core.{Entity, Type}
import tlang.internal.{ClassType, ContextContent}

case class AstEntity(context: Option[ContextContent], model: Option[AstModel], attrs: Option[List[AstEntityAttr]]) extends AstValue {

  override def getType: Type =
    if (model.isDefined) model.get.getType
    else ClassType.of(classOf[Entity])

  override def getElement: AstValue = this

  override def getContext: Option[ContextContent] = context

}
