package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class StyleValue(context: Option[ContextContent]) extends LangNode[StyleValue] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleValue.model

  override def compareTo(value: Value[StyleValue]): Int = 0

  override def getElement: StyleValue = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleValue = StyleValue(context)

  override def getContext: Option[ContextContent] = context
}

object StyleValue {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleValue", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}
