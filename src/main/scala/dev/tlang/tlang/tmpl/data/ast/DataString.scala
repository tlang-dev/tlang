package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class DataString(context: Option[ContextContent]) extends LangNode[DataString] {

  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataString.model

  override def compareTo(value: Value[DataString]): Int = 0

  override def getElement: DataString = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataString(context)

  override def getContext: Option[ContextContent] = context
}

object DataString {
  val model: ModelSetEntity = ModelSetEntity(None, "DataString", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}
