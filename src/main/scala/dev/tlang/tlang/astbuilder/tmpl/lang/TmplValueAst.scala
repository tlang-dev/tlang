package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity

object TmplValueAst {

  val tmplMultiValue: ModelSetEntity = ModelSetEntity(None, "TmplMultiValue", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val tmplValueType: ModelSetEntity = ModelSetEntity(None, "TmplValueType", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val tmplVal: ModelSetEntity = ModelSetEntity(None, "TmplVal", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val tmplVar: ModelSetEntity = ModelSetEntity(None, "TmplVar", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

}
