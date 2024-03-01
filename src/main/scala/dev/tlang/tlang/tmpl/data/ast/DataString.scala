package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataString(context: Option[ContextContent]) extends AstTmplNode {

  override def toEntity: AstEntity = AstEntity(context,
    Some(DataString.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataString.model

  override def getType: Type = DataString.modelName

  //  override def deepCopy(): Any = DataString(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataString = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataString.model
}

object DataString {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}
