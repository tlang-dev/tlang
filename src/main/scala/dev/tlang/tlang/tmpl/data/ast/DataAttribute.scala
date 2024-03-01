package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataAttribute(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataAttribute.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataBlock.model

  override def getType: Type = DataAttribute.modelName

  //  override def deepCopy(): DataBlock = DataBlock(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataAttribute = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataAttribute.model
}

object DataAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)


  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}