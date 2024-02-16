package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class CmdCallFunc(context: Null[ContextContent]) extends TmplNode[CmdCallFunc] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdCallFunc.model

  override def compareTo(value: Value[CmdCallFunc]): Int = 0

  override def getElement: CmdCallFunc = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): CmdCallFunc = CmdCallFunc(context)
}

object CmdCallFunc {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "CmdCallFunc", Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
