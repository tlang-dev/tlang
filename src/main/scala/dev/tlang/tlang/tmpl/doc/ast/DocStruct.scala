package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.{ComplexAttribute, EntityValue}
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

import scala.collection.mutable.ListBuffer

case class DocStruct(context: Null, level: Int, title: String, content: Option[DocContent]) extends DocContentType[DocStruct] {
  //  override def deepCopy(): DocStruct = DocStruct(context, level, new String(title),
  //    if (content.isDefined) Some(content.get.deepCopy()) else None)

  override def getContext: Null = context

  override def getElement: DocStruct = this

  override def getType: Type = DocStruct.modelName

  override def toEntity: EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]
    elems += BuildLang.createAttrInt(context, "level", level)
    elems += BuildLang.createAttrStr(context, "title", title)
//    if (content.isDefined)
//      elems += BuildLang.createAttrEntity(context, "content", content.get.toEntity)
    EntityValue(context,
      Some(ObjType(context, None, DocStruct.modelName)),
      Some(elems.toList)
    )
  }

}

object DocStruct {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
