package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class CmdCallFuncArgs(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(CmdCallFuncArgs.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdCallFuncArgs.model

  override def getType: Type = CmdCallFuncArgs.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): CmdCallFuncArgs = CmdCallFuncArgs(context)

  override def getElement: CmdCallFuncArgs = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CmdCallFuncArgs.model
}

object CmdCallFuncArgs {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(CmdModel.cmdModel), None, Some(List(
  )))
}
