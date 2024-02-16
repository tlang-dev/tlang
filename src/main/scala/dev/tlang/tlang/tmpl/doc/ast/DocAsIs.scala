package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.lang.astbuilder.BuildLang
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocAsIs(context: Null[ContextContent], content: String) extends DocContentType[DocAsIs] {
  override def deepCopy(): DocAsIs = DocAsIs(context, content)

  override def getContext: Null[ContextContent] = context

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
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocAsIs", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}