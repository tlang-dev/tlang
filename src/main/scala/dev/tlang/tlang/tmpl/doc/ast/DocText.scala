package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocText(context: Null[ContextContent], text: DocTextType[_]) extends DocContentType[DocText] {
  override def deepCopy(): DocText = DocText(context, text.deepCopy().asInstanceOf[DocTextType[_]])

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrEntity(context, "text", text.toEntity),
    ))
  )

  override def toModel: ModelSetEntity = DocText.model

  override def compareTo(value: Value[DocText]): Int = 0

  override def getElement: DocText = this

  override def getType: String = getClass.getSimpleName
}

object DocText {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocText", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
