package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity

trait LangContent[TYPE] extends DeepCopy with LangNode[TYPE]


object LangContent {

  val model: ModelSetEntity = ModelSetEntity(None, "LangContent", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}