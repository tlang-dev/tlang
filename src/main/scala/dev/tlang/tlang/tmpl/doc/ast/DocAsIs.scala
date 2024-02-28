package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocAsIs(context: Option[ContextContent], content: String) extends DocContentType[DocAsIs] {
  //  override def deepCopy(): DocAsIs = DocAsIs(context, content)

  override def getContext: Option[ContextContent] = context

  override def getType: Type = DocAsIs.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocAsIs.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "content", content)
    ))
  )

  //  override def toModel: ModelSetEntity = DocAsIs.model

  override def getElement: DocAsIs = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocAsIs.model
}

object DocAsIs {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}