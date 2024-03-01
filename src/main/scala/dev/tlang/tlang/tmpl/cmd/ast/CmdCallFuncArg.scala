package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class CmdCallFuncArg(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(CmdCallFuncArgs.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdCallFuncArg.model

  override def getType: Type = CmdCallFuncArg.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): CmdCallFuncArg = CmdCallFuncArg(context)

  override def getElement: CmdCallFuncArg = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CmdCallFuncArg.model
}

object CmdCallFuncArg {
  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: ManualType = ManualType(getClass.getPackageName, name)


  val model: AstModel = AstModel(None, modelName, Some(CmdModel.cmdModel), None, Some(List(
  )))
}
