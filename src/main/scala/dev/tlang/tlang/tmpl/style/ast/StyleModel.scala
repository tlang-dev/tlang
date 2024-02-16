package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.lang.ast.LangModel
import tlang.core.Null

object StyleModel {

  val styleModel: ModelSetEntity = ModelSetEntity(Null.empty(), "StyleNode", None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("context"), LangModel.langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    StyleArray.model,
    StyleBlock.model,
    StyleInclude.model,
    StyleSetAttribute.model,
  )

}
