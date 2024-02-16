package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocTable(context: Null[ContextContent]) extends DocTextType[DocTable] {
  override def deepCopy(): DocTable = DocTable(context)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocTable]): Int = 0

  override def getElement: DocTable = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = DocTable.model
}

object DocTable {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocTable", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
