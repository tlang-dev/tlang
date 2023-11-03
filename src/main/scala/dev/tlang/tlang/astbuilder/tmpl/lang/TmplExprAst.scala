package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity
import dev.tlang.tlang.astbuilder.tmpl.lang.TmplLangAst.langNode

object TmplExprAst {

  val tmplExprBlock: ModelSetEntity = ModelSetEntity(None, "TmplExprBlock", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplOperation: ModelSetEntity = ModelSetEntity(None, "TmplOperation", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

}
