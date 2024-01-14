package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplNode}

case class DataArray(context: Option[ContextContent]) extends TmplNode[DataArray] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataArray.model

  override def compareTo(value: Value[DataArray]): Int = 0

  override def getElement: DataArray = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataArray(context)

  override def getContext: Option[ContextContent] = context
}

object DataArray {
  val model: ModelSetEntity = ModelSetEntity(None, "DataArray", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}