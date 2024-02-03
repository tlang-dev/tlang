package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class DataInclude(context: Option[ContextContent]) extends TmplNode[DataInclude] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataInclude.model

  override def compareTo(value: Value[DataInclude]): Int = 0

  override def getElement: DataInclude = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataInclude(context)

  override def getContext: Option[ContextContent] = context
}

object DataInclude {
  val model: ModelSetEntity = ModelSetEntity(None, "DataInclude", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}