package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.call.CallObject
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocInclude(context: Null[ContextContent], call: CallObject) extends DocTextType[DocInclude] {

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocInclude.modelName)),
    Some(List())
  )

  override def getElement: DocInclude = this

  override def getType: Type = DocInclude.modelName
}

object DocInclude {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
