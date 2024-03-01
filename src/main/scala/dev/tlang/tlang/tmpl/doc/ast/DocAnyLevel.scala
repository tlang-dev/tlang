package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class DocAnyLevel(context: Option[ContextContent]) extends AstTmplNode {
  //  override def deepCopy(): DocAnyLevel = DocAnyLevel(context)

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocAnyLevel.model),
    Some(List())
  )

  override def getType: Type = DocAnyLevel.modelName

  //  override val toModel: ModelSetEntity = DocAnyLevel.model

  override def getElement: DocAnyLevel = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocAnyLevel.model
}

object DocAnyLevel {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
