package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocPlainText(context: Null[ContextContent], text: String) extends DocTextType[DocPlainText] {
  override def deepCopy(): DocPlainText = DocPlainText(context, new String(text))

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocPlainText]): Int = 0

  override def getElement: DocPlainText = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "text", text)
    ))
  )

  override def toModel: ModelSetEntity = DocPlainText.model
}

object DocPlainText {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocPlainText", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
