package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class CmdCallFuncArgs(context: Null[ContextContent]) extends TmplNode[CmdCallFuncArgs] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CmdCallFuncArgs.modelName)),
    Some(List())
  )

//  override def toModel: ModelSetEntity = CmdCallFuncArgs.model

  override def getType: Type = CmdCallFuncArgs.modelName

  override def getContext: Null[ContextContent] = context

//  override def deepCopy(): CmdCallFuncArgs = CmdCallFuncArgs(context)

  override def getElement: CmdCallFuncArgs = this
}

object CmdCallFuncArgs {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
