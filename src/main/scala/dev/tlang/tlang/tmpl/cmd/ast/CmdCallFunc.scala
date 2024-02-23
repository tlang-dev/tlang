package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.common.{ManualType, ObjType}
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import tlang.core.{Null, Type}
import tlang.internal.{ContextContent, TmplNode}

case class CmdCallFunc(context: Null[ContextContent]) extends TmplNode[CmdCallFunc] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, CmdCallFunc.modelName)),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdCallFunc.model

  override def getType: Type = CmdCallFunc.modelName

  override def getContext: Null[ContextContent] = context

  //  override def deepCopy(): CmdCallFunc = CmdCallFunc(context)

  override def getElement: CmdCallFunc = this
}

object CmdCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: ModelSetEntity = ModelSetEntity(Null.empty(), modelName, Some(ObjType(Null.empty(), None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
