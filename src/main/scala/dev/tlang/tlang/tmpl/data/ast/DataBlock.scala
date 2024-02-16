package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataBlock(context: Null[ContextContent]) extends TmplNode[DataBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataBlock.model

  override def compareTo(value: Value[DataBlock]): Int = 0

  override def getElement: DataBlock = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): DataBlock = DataBlock(context)

  override def getContext: Null[ContextContent] = context
}

object DataBlock {

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataBlock", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}
