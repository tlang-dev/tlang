package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.Null
import tlang.internal.{ContextContent, TmplNode}

case class CmdBlock(context: Null[ContextContent]) extends TmplNode[CmdBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdBlock.model

  override def getType: String = getClass.getSimpleName

  override def getContext: Null[ContextContent] = context

  override def deepCopy(): CmdBlock = CmdBlock(context)
}

object CmdBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: ManualType = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
