package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null

object DocModel {

  val pkg = "tlang.tmpl.dpc"

  val docModel: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "DocNode"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("context"), LangModel.langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
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

