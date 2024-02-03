package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class DataValue(context: Option[ContextContent]) extends TmplNode[DataValue] {
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

  override def getContext: Option[ContextContent] = context
}

object DataValue {
  val model: ModelSetEntity = ModelSetEntity(None, "DataValue", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}
