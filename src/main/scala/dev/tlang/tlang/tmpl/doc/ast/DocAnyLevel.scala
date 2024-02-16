package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocAnyLevel(context: Null[ContextContent]) extends TmplNode[DocAnyLevel] {
  override def deepCopy(): DocAnyLevel = DocAnyLevel(context)

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def compareTo(value: Value[DocAnyLevel]): Int = 0

  override def getElement: DocAnyLevel = this

  override def getType: String = getClass.getSimpleName

  override val toModel: ModelSetEntity = DocAnyLevel.model
}

object DocAnyLevel {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocAnyLevel", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
