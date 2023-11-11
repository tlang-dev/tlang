package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.tmpl.lang.TmplLangAst.langNode

object TmplFuncAst {

  val tmplFunc: ModelSetEntity = ModelSetEntity(None, "TmplFunc", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplFuncParam: ModelSetEntity = ModelSetEntity(None, "TmplFuncParam", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val langAnonFunc: ModelSetEntity = ModelSetEntity(None, "LangAnonFunc", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))
}
