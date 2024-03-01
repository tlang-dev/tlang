package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataSetAttribute(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataSetAttribute.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataSetAttribute.model

  override def getType: Type = DataSetAttribute.modelName

  //  override def deepCopy(): Any = DataSetAttribute(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataSetAttribute = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataSetAttribute.model
}

object DataSetAttribute {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}