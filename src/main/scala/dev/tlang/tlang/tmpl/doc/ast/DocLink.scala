package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocLink(context: Option[ContextContent], src: String, name: String) extends DocTextType[DocLink] {

  override def getContext: Option[ContextContent] = context

  override def getElement: DocLink = this

  override def getType: Type = DocLink.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocLink.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "src", src),
      BuildAstTmpl.createAttrStr(context, "name", name),
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocLink.model
}

object DocLink {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
