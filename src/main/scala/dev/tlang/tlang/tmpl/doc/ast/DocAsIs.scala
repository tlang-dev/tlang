package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang

case class DocAsIs(context: Option[ContextContent], content: String) extends DocContentType[DocAsIs] {
  override def deepCopy(): DocAsIs = DocAsIs(context, content)

  override def getContext: Option[ContextContent] = context

  override def compareTo(value: Value[DocAsIs]): Int = 0

  override def getElement: DocAsIs = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
      BuildLang.createAttrStr(context, "content", content)
    ))
  )

  override def toModel: ModelSetEntity = DocAsIs.model
}

object DocAsIs {
  val model: ModelSetEntity = ModelSetEntity(None, "DocAsIs", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}