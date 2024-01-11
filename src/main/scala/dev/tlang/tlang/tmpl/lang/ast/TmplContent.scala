package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity

trait TmplContent[TYPE] extends DeepCopy with TmplNode[TYPE]


object TmplContent {

  val model: ModelSetEntity = ModelSetEntity(None, "LangContent", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))
}