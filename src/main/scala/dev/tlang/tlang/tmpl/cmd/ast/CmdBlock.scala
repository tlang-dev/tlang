package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class CmdBlock(context: Null[ContextContent]) extends TmplNode[CmdBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CmdBlock.modelName)),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdBlock.model

  override def getType: Type = CmdBlock.modelName

  override def getContext: Null[ContextContent] = context

  //  override def deepCopy(): CmdBlock = CmdBlock(context)

  override def getElement: CmdBlock = this
}

object CmdBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: ManualType = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
