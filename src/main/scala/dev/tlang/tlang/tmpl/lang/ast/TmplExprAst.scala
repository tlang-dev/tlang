package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import TmplLangAst.langNode

object TmplExprAst {

  val tmplExprBlock: ModelSetEntity = ModelSetEntity(None, "TmplExprBlock", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val langOperation: ModelSetEntity = ModelSetEntity(None, "LangOperation", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

}
