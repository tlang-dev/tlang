package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocImg(context: Option[ContextContent], src: String, alt: Option[String]) extends DocTextType[DocImg] {

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocImg.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "src", src),
      BuildAstTmpl.createAttrStr(context, "alt", alt.get),
    ))
  )

  override def getElement: DocImg = this

  override def getType: Type = DocImg.modelName

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocImg.model
}

object DocImg {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}