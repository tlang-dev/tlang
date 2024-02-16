package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataValue(context: Null[ContextContent]) extends TmplNode[DataValue] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataValue.model

  override def compareTo(value: Value[DataValue]): Int = 0

  override def getElement: DataValue = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): DataValue = DataValue(context)

  override def getContext: Null[ContextContent] = context
}

object DataValue {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataValue", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}
