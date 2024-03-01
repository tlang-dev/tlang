package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class CmdName(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(CmdName.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdName.model

  override def getType: Type = CmdName.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): CmdName = CmdName(context)

  override def getElement: CmdName = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CmdName.model
}

object CmdName {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(CmdModel.cmdModel), None, Some(List(
  )))
}
