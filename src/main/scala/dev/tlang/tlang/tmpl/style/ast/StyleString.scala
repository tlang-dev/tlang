package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class StyleString(context: Option[ContextContent]) extends LangNode[StyleString] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleString.model

  override def compareTo(value: Value[StyleString]): Int = 0

  override def getElement: StyleString = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleString = StyleString(context)

  override def getContext: Option[ContextContent] = context
}

object StyleString {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleString", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}