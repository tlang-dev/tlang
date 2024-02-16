package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataSetAttribute(context: Null[ContextContent]) extends TmplNode[DataSetAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataSetAttribute.model

  override def compareTo(value: Value[DataSetAttribute]): Int = 0

  override def getElement: DataSetAttribute = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataSetAttribute(context)

  override def getContext: Null[ContextContent] = context
}

object DataSetAttribute {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataSetAttribute", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}