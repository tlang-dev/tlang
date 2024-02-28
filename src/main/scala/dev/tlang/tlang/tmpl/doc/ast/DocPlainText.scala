package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocPlainText(context: Option[ContextContent], text: String) extends DocTextType[DocPlainText] {

  override def getContext: Option[ContextContent] = context

  override def getElement: DocPlainText = this

  override def getType: Type = DocPlainText.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocPlainText.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "text", text)
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocPlainText.model
}

object DocPlainText {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
