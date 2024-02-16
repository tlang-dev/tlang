package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataString(context: Null[ContextContent]) extends TmplNode[DataString] {

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataString.model

  override def compareTo(value: Value[DataString]): Int = 0

  override def getElement: DataString = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataString(context)

  override def getContext: Null[ContextContent] = context
}

object DataString {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataString", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}
