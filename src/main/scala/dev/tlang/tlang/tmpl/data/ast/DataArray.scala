package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataArray(context: Null[ContextContent]) extends TmplNode[DataArray] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataArray.model

  override def compareTo(value: Value[DataArray]): Int = 0

  override def getElement: DataArray = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataArray(context)

  override def getContext: Null[ContextContent] = context
}

object DataArray {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataArray", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}