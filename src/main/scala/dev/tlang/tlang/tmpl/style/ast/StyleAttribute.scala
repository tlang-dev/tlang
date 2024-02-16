package dev.tlang.tlang.tmpl.style.ast

import tlang.internal.TmplNode

trait StyleAttribute[T] extends TmplNode[T] {


}

/*
case class StyleAttribute(context: Option[ContextContent]) extends LangNode[StyleAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleAttribute.model

  override def compareTo(value: Value[StyleAttribute]): Int = 0

  override def getElement: StyleAttribute = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleAttribute = StyleAttribute(context)

  override def getContext: Option[ContextContent] = context
}

object StyleAttribute {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleAttribute", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}*/