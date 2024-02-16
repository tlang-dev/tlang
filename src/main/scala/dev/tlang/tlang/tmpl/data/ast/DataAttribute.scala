package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataAttribute(context: Null[ContextContent]) extends TmplNode[DataAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataBlock.model

  override def compareTo(value: Value[DataAttribute]): Int = 0

  override def getElement: DataAttribute = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): DataBlock = DataBlock(context)

  override def getContext: Null[ContextContent] = context
}

object DataAttribute{
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataAttribute", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}