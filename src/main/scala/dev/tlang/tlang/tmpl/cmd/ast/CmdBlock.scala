package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.EntityValue
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.context.ContextContent
import dev.tlang.tlang.interpreter.Value
import dev.tlang.tlang.tmpl.lang.ast.LangNode

case class CmdBlock(context: Option[ContextContent]) extends LangNode[CmdBlock] {
  override def toEntity: EntityValue = EntityValue(context,
    Some(ObjType(context, None, toModel.name)),
    Some(List())
  )

  override def toModel: ModelSetEntity = CmdBlock.model

  override def compareTo(value: Value[CmdBlock]): Int = 0

  override def getElement: CmdBlock = this

  override def getType: String = getClass.getSimpleName

  override def getContext: Option[ContextContent] = context

  override def deepCopy(): CmdBlock = CmdBlock(context)
}

object CmdBlock {
  val model: ModelSetEntity = ModelSetEntity(None, "CmdBlock", Some(ObjType(None, None, CmdModel.cmdModel.name)), None, Some(List(
  )))
}
