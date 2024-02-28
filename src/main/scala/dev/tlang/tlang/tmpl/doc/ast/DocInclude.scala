package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocInclude(context: Option[ContextContent], call: CallObject) extends DocTextType[DocInclude] {

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocInclude.model),
    Some(List())
  )

  override def getElement: DocInclude = this

  override def getType: Type = DocInclude.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocInclude.model
}

object DocInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
