package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class CmdCallFuncArgs(context: Null[ContextContent]) extends TmplNode[CmdCallFuncArgs] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdCallFuncArgs.model

  override def compareTo(value: Value[CmdCallFuncArgs]): Int = 0

  override def getElement: CmdCallFuncArgs = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): CmdCallFuncArgs = CmdCallFuncArgs(context)
}

object CmdCallFuncArgs {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "CmdCallFuncArgs", Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
