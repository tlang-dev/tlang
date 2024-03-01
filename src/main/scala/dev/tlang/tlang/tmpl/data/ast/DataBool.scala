package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataBool(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataBool.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataBool.model

  override def getType: Type = DataBool.modelName

  override def getContext: Option[ContextContent] = context

  override def getElement: DataBool = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataBool.model
}

object DataBool {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}