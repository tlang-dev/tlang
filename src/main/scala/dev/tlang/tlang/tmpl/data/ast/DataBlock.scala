package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class DataBlock(context: Option[ContextContent]) extends TmplNode[DataBlock] {
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

  override def getContext: Option[ContextContent] = context
}

object DataBlock {

  val model: ModelSetEntity = ModelSetEntity(None, "DataBlock", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}
