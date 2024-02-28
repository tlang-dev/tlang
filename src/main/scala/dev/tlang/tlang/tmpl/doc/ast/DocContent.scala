package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.{ContextContent, TmplNode}

case class DocContent(context: Option[ContextContent], contents: List[DocContentType[_]]) extends TmplNode[DocContent] {
  //  override def deepCopy(): DocContent = DocContent(context, contents.map(_.deepCopy().asInstanceOf[DocContentType[_]]))

  override def getContext: Option[ContextContent] = context

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocContent.model),
    Some(List(
      BuildAstTmpl.createAttrList(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def getType: Type = DocContent.modelName

  override def getElement: DocContent = this
}

object DocContent {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}