package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value

case class DocInclude(context: Option[ContextContent], call: CallObject) extends DocTextType[DocInclude] {
  override def deepCopy(): DocInclude = DocInclude(context, call)

  override def getContext: Option[ContextContent] = context

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
  val model: ModelSetEntity = ModelSetEntity(None, "DocInclude", Some(ObjType(None, None, DocModel.docModel.name)), None, Some(List(
  )))
}
