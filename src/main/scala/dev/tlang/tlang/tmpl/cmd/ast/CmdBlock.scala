package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class CmdBlock(context: Option[ContextContent], name: String) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(CmdBlock.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdBlock.model

  override def getType: Type = CmdBlock.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): CmdBlock = CmdBlock(context)

  override def getElement: CmdBlock = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CmdBlock.model
}

object CmdBlock {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: ManualType = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(CmdModel.cmdModel), None, Some(List(
  )))
}
