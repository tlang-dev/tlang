package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocSpan(context: Null) extends DocTextType[DocSpan] {

  override def getContext: Null = context

  override def getElement: DocSpan = this

  override def getType: Type = DocSpan.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocSpan.modelName)),
    Some(List())
  )

}

object DocSpan {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
