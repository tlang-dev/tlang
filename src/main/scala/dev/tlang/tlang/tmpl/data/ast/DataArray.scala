package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataArray(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataArray.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataArray.model

  override def getType: Type = DataArray.modelName

  //  override def deepCopy(): Any = DataArray(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataArray = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataArray.model
}

object DataArray {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}