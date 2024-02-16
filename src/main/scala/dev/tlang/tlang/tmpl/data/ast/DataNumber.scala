package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataNumber(context: Null[ContextContent]) extends TmplNode[DataNumber] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataNumber.model

  override def compareTo(value: Value[DataNumber]): Int = 0

  override def getElement: DataNumber = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataNumber(context)

  override def getContext: Null[ContextContent] = context
}

object DataNumber {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataNumber", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}
