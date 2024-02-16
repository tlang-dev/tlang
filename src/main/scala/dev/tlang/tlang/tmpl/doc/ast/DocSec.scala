package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocSec(context: Null[ContextContent], title: String, content: DocContent) extends DocContentType[DocSec] {
  override def deepCopy(): DocSec = DocSec(context, new String(title), content.deepCopy())

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocSec]): Int = 0

  override def getElement: DocSec = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = DocSec.model
}

object DocSec {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocSec", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
