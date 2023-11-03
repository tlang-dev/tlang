package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.tmpl.lang.TmplLangAst.langNode

object TmplLoopAst {

  val tmplFor: ModelSetEntity = ModelSetEntity(None, "TmplFor", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplWhile: ModelSetEntity = ModelSetEntity(None, "TmplWhile", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplDoWhile: ModelSetEntity = ModelSetEntity(None, "TmplDoWhile", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

}
