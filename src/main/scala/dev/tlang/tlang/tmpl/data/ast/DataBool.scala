package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DataBool(context: Null[ContextContent]) extends TmplNode[DataBool] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DataBool.modelName)),
    Some(List(
    ))
  )

//  override def toModel: ModelSetEntity = DataBool.model

  override def getType: Type = DataBool.modelName

  override def getContext: Null[ContextContent] = context

  override def getElement: DataBool = this
}

object DataBool {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}