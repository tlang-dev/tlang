package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class DataNumber(context: Option[ContextContent]) extends TmplNode[DataNumber] {
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

  override def getContext: Option[ContextContent] = context
}

object DataNumber {
  val model: ModelSetEntity = ModelSetEntity(None, "DataNumber", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}
