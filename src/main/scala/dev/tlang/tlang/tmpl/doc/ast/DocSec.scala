package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocSec(context: Option[ContextContent], title: String, content: DocContent) extends DocContentType[DocSec] {
  //  override def deepCopy(): DocSec = DocSec(context, new String(title), content.deepCopy())

  override def getContext: Option[ContextContent] = context

  override def getElement: DocSec = this

  override def getType: Type = DocSec.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocSec.model),
    Some(List())
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocSec.model
}

object DocSec {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
