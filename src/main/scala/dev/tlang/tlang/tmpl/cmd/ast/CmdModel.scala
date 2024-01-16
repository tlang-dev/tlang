package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import .langContext

object CmdModel {
  val cmdModel: ModelSetEntity = ModelSetEntity(None, "CmdNode", None, None, Some(List(
    ModelSetAttribute(None, Some("context"), langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    CmdBlock.model,
    CmdCallFunc.model,
    CmdCallFuncArg.model,
    CmdCallFuncArgs.model,
    CmdName.model
  )
}
