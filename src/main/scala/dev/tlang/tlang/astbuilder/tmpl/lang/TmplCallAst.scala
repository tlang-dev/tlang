package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.tmpl.lang.TmplLangAst.langNode

object TmplCallAst {

  val tmplCallArray: ModelSetEntity = ModelSetEntity(None, "TmplCallArray", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplCallFunc: ModelSetEntity = ModelSetEntity(None, "TmplCallFunc", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplCallFuncParam: ModelSetEntity = ModelSetEntity(None, "TmplCallFuncParam", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplCallObj: ModelSetEntity = ModelSetEntity(None, "TmplCallObj", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplCallObjLink: ModelSetEntity = ModelSetEntity(None, "TmplCallObjLink", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplCallObjVar: ModelSetEntity = ModelSetEntity(None, "TmplCallObjVar", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))
}
