package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataNumber(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataNumber.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataNumber.model

  override def getType: Type = DataNumber.modelName

  //  override def deepCopy(): Any = DataNumber(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataNumber = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataNumber.model
}

object DataNumber {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}
