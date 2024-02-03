package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class CmdCallFuncArg(context: Option[ContextContent]) extends TmplNode[CmdCallFuncArg] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdCallFuncArg.model

  override def compareTo(value: Value[CmdCallFuncArg]): Int = 0

  override def getElement: CmdCallFuncArg = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): CmdCallFuncArg = CmdCallFuncArg(context)
}

object CmdCallFuncArg {
  val model: ModelSetEntity = ModelSetEntity(None, "CmdCallFuncArg", Some(ObjType(None, None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
