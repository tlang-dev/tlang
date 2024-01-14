package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleBool(context: Option[ContextContent]) extends TmplNode[StyleBool] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleBool.model

  override def compareTo(value: Value[StyleBool]): Int = 0

  override def getElement: StyleBool = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleBool = StyleBool(context)

  override def getContext: Option[ContextContent] = context
}

object StyleBool {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleBool", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}