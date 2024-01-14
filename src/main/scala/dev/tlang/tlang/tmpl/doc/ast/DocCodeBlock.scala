package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocCodeBlock(context: Option[ContextContent], lang: String, code: String) extends DocTextType[DocCodeBlock] {
  override def deepCopy(): DocCodeBlock = DocCodeBlock(context, new String(lang), new String(code))

  override def getContext: Option[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "language", lang),
      BuildLang.createAttrStr(context, "code", code)
    ))
  )

  override def toModel: ModelSetEntity = DocCodeBlock.model

  override def compareTo(value: Value[DocCodeBlock]): Int = 0

  override def getElement: DocCodeBlock = this

  override def getType: String = getClass.getSimpleName
}

object DocCodeBlock {
  val model: ModelSetEntity = ModelSetEntity(None, "DocCodeBlock", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}