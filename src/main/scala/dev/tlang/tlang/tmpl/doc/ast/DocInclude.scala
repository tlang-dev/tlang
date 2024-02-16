package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DocInclude(context: Null[ContextContent], call: CallObject) extends DocTextType[DocInclude] {
  override def deepCopy(): DocInclude = DocInclude(context, call)

  override def getContext: Null[ContextContent] = context

  override def compareTo(value: Value[DocInclude]): Int = 0

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = DocInclude.model

  override def getElement: DocInclude = this

  override def getType: String = getClass.getSimpleName
}

object DocInclude {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DocInclude", Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
