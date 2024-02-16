package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataBool(context: Null[ContextContent]) extends TmplNode[DataBool] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataBool.model

  override def compareTo(value: Value[DataBool]): Int = 0

  override def getElement: DataBool = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataBool(context)

  override def getContext: Null[ContextContent] = context
}

object DataBool {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataBool", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}