package dev.tlang.tlang.tmpl.data.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.{TmplLangAst, TmplNode}

case class DataBool(context: Option[ContextContent]) extends TmplNode[DataBool] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List(
    ))
  )

  override def toModel: ModelSetEntity = DataBool.model

  override def compareTo(value: Value[DataBool]): Int = 0

  override def getElement: DataBool = this

  override def getType: String = getClass.getSimpleName

  override def deepCopy(): Any = DataBool(context)

  override def getContext: Option[ContextContent] = context
}

object DataBool {
  val model: ModelSetEntity = ModelSetEntity(None, "DataBool", Some(ObjType(None, None, DataModel.dataModel.name)), None, Some(List(
  )))
}