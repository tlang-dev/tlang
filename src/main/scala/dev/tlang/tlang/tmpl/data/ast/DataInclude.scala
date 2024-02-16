package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class DataInclude(context: Null[ContextContent]) extends TmplNode[DataInclude] {
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

  override def getContext: Null[ContextContent] = context
}

object DataInclude {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "DataInclude", Some(ObjType(Null.empty(), None, DataModel.dataModel.name)), None, Some(List(
  )))
}