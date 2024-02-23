package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DataAttribute(context: Null[ContextContent]) extends TmplNode[DataAttribute] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DataAttribute.modelName)),
    Some(List(
    ))
  )

//  override def toModel: ModelSetEntity = DataBlock.model

  override def getType: Type = DataAttribute.modelName

//  override def deepCopy(): DataBlock = DataBlock(context)

  override def getContext: Null[ContextContent] = context

  override def getElement: DataAttribute = this
}

object DataAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}