package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.tmpl.TmplNode

trait LangContent[TYPE] extends TmplNode[TYPE]


object LangContent {

  val model: ModelSetEntity = ModelSetEntity(None, "LangContent", Some(ObjType(None, None, LangModel.langNode.name)), None, Some(List(
  )))
}