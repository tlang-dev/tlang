package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class CmdCallFuncArg(context: Null) extends TmplNode[CmdCallFuncArg] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CmdCallFuncArgs.modelName)),
    Some(List())
  )

//  override def toModel: ModelSetEntity = CmdCallFuncArg.model

  override def getType: Type = CmdCallFuncArg.modelName

  override def getContext: Null = context

//  override def deepCopy(): CmdCallFuncArg = CmdCallFuncArg(context)

  override def getElement: CmdCallFuncArg = this
}

object CmdCallFuncArg {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: ManualType = ManualType(getClass.getPackageName, name)


  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
