package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.TmplNode

case class StyleNumber(context: Option[ContextContent]) extends TmplNode[StyleNumber] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = ???

  override def compareTo(value: Value[StyleNumber]): Int = ???

  override def getElement: StyleNumber = ???

  override def getType: String = ???

  override def deepCopy(): StyleNumber = ???

  override def getContext: Option[ContextContent] = context
}

object StyleNumber {
  val model: ModelSetEntity = ModelSetEntity(None, "StyleNumber", Some(ObjType(None, None, StyleModel.styleModel.name)), None, Some(List(
  )))
}