package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DataValue(context: Null[ContextContent]) extends TmplNode[DataValue] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DataValue.modelName)),
    Some(List(
    ))
  )

//  override def toModel: ModelSetEntity = DataValue.model

  override def getType: Type = DataValue.modelName

//  override def deepCopy(): DataValue = DataValue(context)

  override def getContext: Null[ContextContent] = context

  override def getElement: DataValue = this
}

object DataValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}
