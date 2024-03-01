package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocText(context: Option[ContextContent], text: DocTextType[_]) extends DocContentType[DocText] {
  //  override def deepCopy(): DocText = DocText(context, text.deepCopy().asInstanceOf[DocTextType[_]])

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocText.model),
    Some(List(
      //      BuildAstTmpl.createAttrEntity(context, "text", text.toEntity),
    ))
  )

  //  override def toModel: ModelSetEntity = DocText.model

  override def getElement: DocText = this

  override def getType: Type = DocText.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocText.model
}

object DocText {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
