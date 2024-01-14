package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleSetAttribute(context: Option[ContextContent]) extends TmplNode[StyleSetAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleSetAttribute.model

  override def compareTo(value: Value[StyleSetAttribute]): Int = 0

  override def getElement: StyleSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleSetAttribute = StyleSetAttribute(context)

  override def getContext: Option[ContextContent] = context
}

object StyleSetAttribute {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleSetAttribute", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}