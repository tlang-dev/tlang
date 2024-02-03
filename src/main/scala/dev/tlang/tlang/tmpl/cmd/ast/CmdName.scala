package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.TmplNode

case class CmdName(context: Option[ContextContent]) extends TmplNode[CmdName] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdName.model

  override def compareTo(value: Value[CmdName]): Int = 0

  override def getElement: CmdName = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): CmdName = CmdName(context)
}

object CmdName {
  val model: ModelSetEntity = ModelSetEntity(None, "CmdName", Some(ObjType(None, None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
