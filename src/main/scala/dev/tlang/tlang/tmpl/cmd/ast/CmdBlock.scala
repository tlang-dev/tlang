package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.internal.TmplNode
import tlang.core.{Null, Value}
import tlang.internal.ContextContent

case class CmdBlock(context: Null[ContextContent]) extends TmplNode[CmdBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdBlock.model

  override def compareTo(value: Value[CmdBlock]): Int = 0

  override def getElement: CmdBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): CmdBlock = CmdBlock(context)
}

object CmdBlock {
  val model: ModelSetEntity = ModelSetEntity(Null.empty(), "CmdBlock", Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
