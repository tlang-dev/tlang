package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

case class DocList(context: Option[ContextContent], order: String, contents: List[DocContent]) extends DocTextType[DocList] {
  //  override def deepCopy(): DocList = DocList(context, new String(order), contents.map(_.deepCopy()))

  override def getContext: Option[ContextContent] = context

  override def getElement: DocList = this

  override def getType: Type = DocList.modelName

  override def toEntity: AstEntity = AstEntity(context,
    Some(DocList.model),
    Some(List(
      BuildAstTmpl.createAttrStr(context, "order", order),
      //      BuildLang.createArray(context, "contents", contents.map(_.toEntity))
    ))
  )

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocList.model
}

object DocList {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
