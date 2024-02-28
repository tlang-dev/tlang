package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import dev.tlang.tlang.tmpl.{AstModel, BuildAstTmpl}

object DocModel {

  val pkg = "tlang.tmpl.dpc"

  val docModel: AstModel = AstModel(None, ManualType(pkg, "DocNode"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("context"), LangModel.langContext.getType)
  )))

  val getAll: List[AstModel] = List(
    DocAnyLevel.model,
    DocAsIs.model,
    DocBlock.model,
    DocCodeBlock.model,
    DocContent.model,
    DocImg.model,
    DocInclude.model,
    DocLink.model,
    DocList.model,
    DocPlainText.model,
    DocSec.model,
    DocSpan.model,
    DocPlainText.model,
    DocStruct.model,
    DocTable.model,
    DocText.model
  )
}

