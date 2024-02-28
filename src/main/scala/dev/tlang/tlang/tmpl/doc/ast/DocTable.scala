package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocTable(context: Option[ContextContent]) extends DocTextType[DocTable] {

  override def getContext: Option[ContextContent] = context

  override def getElement: DocTable = this

  override def getType: Type = DocTable.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocTable.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = DocTable.model

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocTable.model
}

object DocTable {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
