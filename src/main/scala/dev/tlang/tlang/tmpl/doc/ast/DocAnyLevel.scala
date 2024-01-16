package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.data.ast.DataModel
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class DocAnyLevel(context: Option[ContextContent]) extends LangNode[DocAnyLevel] {
  override def deepCopy(): DocAnyLevel = DocAnyLevel(context)

  override def getContext: Option[ContextContent] = context

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
  val model: ModelSetEntity = ModelSetEntity(None, "DocAnyLevel", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}
