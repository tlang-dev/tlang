package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class StyleBlock(context: Option[ContextContent]) extends LangNode[StyleBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleBlock.model

  override def compareTo(value: Value[StyleBlock]): Int = 0

  override def getElement: StyleBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): StyleBlock = StyleBlock(context)
}

object StyleBlock {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleBlock", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}
