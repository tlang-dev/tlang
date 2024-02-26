package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.ContextContent

case class DocTable(context: Null) extends DocTextType[DocTable] {

  override def getContext: Null = context

  override def getElement: DocTable = this

  override def getType: Type = DocTable.modelName

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocTable.modelName)),
    Some(List())
  )

//  override def toModel: ModelSetEntity = DocTable.model
}

object DocTable {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(DocModel.pkg, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
