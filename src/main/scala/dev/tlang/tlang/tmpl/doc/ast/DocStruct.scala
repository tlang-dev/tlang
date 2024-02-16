package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

import scala.collection.mutable.ListBuffer

case class DocStruct(context: Null[ContextContent], level: Int, title: String, content: Option[DocContent]) extends DocContentType[DocStruct] {
  override def deepCopy(): DocStruct = DocStruct(context, level, new String(title),
    if (content.isDefined) Some(content.get.deepCopy()) else None)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocStruct]): Int = 0

  override def getElement: DocStruct = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]
    elems += BuildLang.createAttrInt(context, "level", level)
    elems += BuildLang.createAttrStr(context, "title", title)
    if (content.isDefined)
      elems += BuildLang.createAttrEntity(context, "content", content.get.toEntity)
    EntityValue(context,
      Some(ObjType(context, None, toModel.name)),
      Some(elems.toList)
    )
  }

  override def toModel: ModelSetEntity = DocStruct.model
}

object DocStruct {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocStruct", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
