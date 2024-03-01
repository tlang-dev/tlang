package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class DataInclude(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(DataInclude.model),
    Some(List(
    ))
  )

  //  override def toModel: ModelSetEntity = DataInclude.model

  override def getType: Type = DataInclude.modelName

  //  override def deepCopy(): Any = DataInclude(context)

  override def getContext: Option[ContextContent] = context

  override def getElement: DataInclude = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DataInclude.model
}

object DataInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DataModel.dataModel), None, Some(List(
  )))
}