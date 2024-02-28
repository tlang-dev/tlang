package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocSpan(context: Option[ContextContent]) extends DocTextType[DocSpan] {

  override def getContext: Option[ContextContent] = context

  override def getElement: DocSpan = this

  override def getType: Type = DocSpan.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocSpan.model),
    Some(List())
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocSpan.model
}

object DocSpan {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
