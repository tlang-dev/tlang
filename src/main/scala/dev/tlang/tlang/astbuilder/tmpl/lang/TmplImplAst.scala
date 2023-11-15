package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.astbuilder.tmpl.lang.TmplLangAst.{langNode, langPkg}

object TmplImplAst {

  val tmplImplFor: ModelSetEntity = ModelSetEntity(None, "TmplImplFor", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplImplWith: ModelSetEntity = ModelSetEntity(None, "TmplImplWith", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))


  val langImpl: ModelSetEntity = ModelSetEntity(None, "LangImpl", None, None, Some(List(
    ModelSetAttribute(None, Some("annots"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("props"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("fors"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("withs"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("content"), ModelSetType(None, langPkg.name)),
  )))

}
