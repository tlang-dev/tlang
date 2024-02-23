package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class DocAnyLevel(context: Null[ContextContent]) extends TmplNode[DocAnyLevel] {
  //  override def deepCopy(): DocAnyLevel = DocAnyLevel(context)

  override def getContext: Null[ContextContent] = context

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, DocAnyLevel.modelName)),
    Some(List())
  )

  override def getType: Type = DocAnyLevel.modelName

  //  override val toModel: ModelSetEntity = DocAnyLevel.model

  override def getElement: DocAnyLevel = this
}

object DocAnyLevel {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, DocModel.docModel.name)), None, Some(List(
  )))
}
