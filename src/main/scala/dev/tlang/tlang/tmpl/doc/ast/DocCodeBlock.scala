package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocCodeBlock(context: Option[ContextContent], lang: String, code: String) extends DocTextType[DocCodeBlock] {

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocCodeBlock.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "language", lang),
      BuildAstTmpl.createAttrStr(context, "code", code)
    ))
  )


  override def getElement: DocCodeBlock = this

  override def getType: Type = DocCodeBlock.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocCodeBlock.model
}

object DocCodeBlock {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}