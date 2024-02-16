package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class CmdCallFuncArg(context: Null[ContextContent]) extends TmplNode[CmdCallFuncArg] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdCallFuncArg.model

  override def compareTo(value: Value[CmdCallFuncArg]): Int = 0

  override def getElement: CmdCallFuncArg = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): CmdCallFuncArg = CmdCallFuncArg(context)
}

object CmdCallFuncArg {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "CmdCallFuncArg", Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
