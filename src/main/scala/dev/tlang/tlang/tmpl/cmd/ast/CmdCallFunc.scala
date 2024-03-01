package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.{AstEntity, AstModel, AstTmplNode}
import tlang.core.Type
import tlang.internal.ContextContent

case class CmdCallFunc(context: Option[ContextContent]) extends AstTmplNode {
  override def toEntity: AstEntity = AstEntity(context,
    Some(CmdCallFunc.model),
    Some(List())
  )

  //  override def toModel: ModelSetEntity = CmdCallFunc.model

  override def getType: Type = CmdCallFunc.modelName

  override def getContext: Option[ContextContent] = context

  //  override def deepCopy(): CmdCallFunc = CmdCallFunc(context)

  override def getElement: CmdCallFunc = this

  override def getName: String = getClass.getSimpleName

  override def toModel: AstModel = CmdCallFunc.model
}

object CmdCallFunc {

  val name: String = this.getClass.getSimpleName.replace("$", "")

  val modelName: Type = ManualType(getClass.getPackageName, name)

  val model: AstModel = AstModel(None, modelName, Some(CmdModel.cmdModel), None, Some(List(
  )))
}
