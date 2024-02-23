package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DataArray(context: Null[ContextContent]) extends TmplNode[DataArray] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DataArray.modelName)),
    Some(List(
    ))
  )

//  override def toModel: ModelSetEntity = DataArray.model

  override def getType: Type = DataArray.modelName

//  override def deepCopy(): Any = DataArray(context)

  override def getContext: Null[ContextContent] = context

  override def getElement: DataArray = this
}

object DataArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}