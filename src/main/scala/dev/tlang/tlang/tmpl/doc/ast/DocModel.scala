package dev.tlang.tlang.tmpl.doc.ast

import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import .langContext

object DocModel {
  val docModel: ModelSetEntity = ModelSetEntity(None, "DocNode", None, None, Some(List(
    ModelSetAttribute(None, Some("context"), langContext)
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

