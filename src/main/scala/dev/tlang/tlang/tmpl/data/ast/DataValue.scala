package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataValue(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataValue.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataValue.model

  override def getType: Type = DataValue.modelName

  //  override def deepCopy(): DataValue = DataValue(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataValue = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataValue.model
}

object DataValue {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}
