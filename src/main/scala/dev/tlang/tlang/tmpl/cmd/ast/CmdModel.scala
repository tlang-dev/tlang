package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null

object CmdModel {

  val pkg = "tlang.tmpl.cmd"

  val cmdModel: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "CmdNode"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("context"), LangModel.langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    CmdBlock.model,
    CmdCallFunc.model,
    CmdCallFuncArg.model,
    CmdCallFuncArgs.model,
    CmdName.model
  )
}
