package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.TLangLong
import dev.tlang.tlang.tmpl.{AstEntity, AstEntityAttr, AstModel, BuildAstTmpl}
import tlang.core.Type
import tlang.internal.ContextContent

import scala.collection.mutable.ListBuffer

case class DocStruct(context: Option[ContextContent], level: Int, title: String, content: Option[DocContent]) extends DocContentType[DocStruct] {
  //  override def deepCopy(): DocStruct = DocStruct(context, level, new String(title),
  //    if (content.isDefined) Some(content.get.deepCopy()) else None)

  override def getContext: Option[ContextContent] = context

  override def getElement: DocStruct = this

  override def getType: Type = DocStruct.modelName

  override def toEntity: AstEntity = {
    val elems = ListBuffer.empty[AstEntityAttr]
    elems += BuildAstTmpl.createAttrLong(context, "level", new TLangLong(None, level.toLong))
    elems += BuildAstTmpl.createAttrStr(context, "title", title)
    //    if (content.isDefined)
    //      elems += BuildLang.createAttrEntity(context, "content", content.get.toEntity)
    AstEntity(context,
      Some(DocStruct.model),
      Some(elems.toList)
    )
  }

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = DocStruct.model
}

object DocStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: AstModel = AstModel(None, modelName, Some(DocModel.docModel), None, Some(List(
  )))
}
