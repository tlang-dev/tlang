package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocSpan(context: Null[ContextContent]) extends DocTextType[DocSpan] {
  override def deepCopy(): DocSpan = DocSpan(context)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocSpan]): Int = 0

  override def getElement: DocSpan = this

  override def getType: String = getClass.getSimpleName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = DocSpan.model
}

object DocSpan {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocSpan", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
