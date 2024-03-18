package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataBlock(context: Option[ContextContent], name: String) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataBlock.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataBlock.model


  override def getType: Type = DataBlock.modelName

  //  override def deepCopy(): DataBlock = DataBlock(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataBlock = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataBlock.model
}

object DataBlock {


  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}
