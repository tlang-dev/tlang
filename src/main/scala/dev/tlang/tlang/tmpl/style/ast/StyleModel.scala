package dev.tlang.tlang.tmpl.style.ast

import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity}
import dev.tlang.tlang.tmpl.lang.ast.TmplLangAst.langContext

object StyleModel {

  val styleModel: ModelSetEntity = ModelSetEntity(None, "StyleNode", None, None, Some(List(
    ModelSetAttribute(None, Some("context"), langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    StyleArray.model,
    StyleAttribute.model,
    StyleBlock.model,
    StyleBool.model,
    StyleInclude.model,
    StyleNumber.model,
    StyleSetAttribute.model,
    StyleString.model,
    StyleValue.model
  )

}
