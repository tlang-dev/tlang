package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class DocSpan(context: Option[ContextContent]) extends DocTextType[DocSpan] {
  override def deepCopy(): DocSpan = DocSpan(context)

  override def getContext: Option[ContextContent] = context

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
  val model: ModelSetEntity = ModelSetEntity(None, "DocSpan", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}
