package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleInclude(context: Option[ContextContent]) extends TmplNode[StyleInclude] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = StyleInclude.model

  override def compareTo(value: Value[StyleInclude]): Int = 0

  override def getElement: StyleInclude = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): StyleInclude = StyleInclude(context)

  override def getContext: Option[ContextContent] = context
}

object StyleInclude {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleInclude", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}
