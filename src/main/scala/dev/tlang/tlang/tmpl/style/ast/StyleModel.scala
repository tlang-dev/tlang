package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstModel, BuildAstTmpl}

object StyleModel {

  val pkg = "tlang.tmpl.style"

  val styleModel: AstModel = AstModel(None, ManualType(pkg, "StyleNode"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("context"), LangModel.langContext.getType)
  )))

  val getAll: List[AstModel] = List(
    StyleArray.model,
    StyleBlock.model,
    StyleInclude.model,
    StyleSetAttribute.model,
  )

}
