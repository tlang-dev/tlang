package dev.tlang.tlang.tmpl.cmd.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.{AstModel, BuildAstTmpl}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null

object CmdModel {

  val pkg = "tlang.tmpl.cmd"

  val cmdModel: AstModel = AstModel(None, ManualType(pkg, "CmdNode"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("context"), LangModel.langContext.getType),
  )))

  val getAll: List[AstModel] = List(
    CmdBlock.model,
    CmdCallFunc.model,
    CmdCallFuncArg.model,
    CmdCallFuncArgs.model,
    CmdName.model
  )
}
