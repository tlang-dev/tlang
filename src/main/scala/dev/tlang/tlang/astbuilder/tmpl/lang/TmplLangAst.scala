package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{TLangDouble, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetArray, ModelSetAttribute, ModelSetEntity, ModelSetType}

object TmplLangAst {

  val langResource: ModelSetEntity = ModelSetEntity(None, "LangResource", None, None, Some(List(
    ModelSetAttribute(None, Some("rootDir"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("fromRoot"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("pkg"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("name"), ModelSetType(None, TLangString.getType)),
  )))

  val langContext: ModelSetEntity = ModelSetEntity(None, "LangContext", None, None, Some(List(
    ModelSetAttribute(None, Some("resource"), langResource),
    ModelSetAttribute(None, Some("line"), ModelSetType(None, TLangDouble.getType)),
    ModelSetAttribute(None, Some("charPos"), ModelSetType(None, TLangDouble.getType)),

  )))

  val langNode: ModelSetEntity = ModelSetEntity(None, "LangNode", None, None, Some(List(
    ModelSetAttribute(None, Some("context"), langContext)
  )))

  val langPkg: ModelSetEntity = ModelSetEntity(None, "LangPkg", None, None, Some(List(
    ModelSetAttribute(None, Some("parts"), ModelSetArray(None, TLangString.getType))
  )))

  val langUse: ModelSetEntity = ModelSetEntity(None, "LangUse", None, None, Some(List(
    ModelSetAttribute(None, Some("parts"), ModelSetArray(None, TLangString.getType)),
    ModelSetAttribute(None, Some("as"), ModelSetType(None, TLangString.getType)),
  )))

  val langFullBlock: ModelSetEntity = ModelSetEntity(None, "LangFullBlock", None, None, Some(List(
    ModelSetAttribute(None, Some("pkg"), ModelSetType(None, langPkg.name)),
    ModelSetAttribute(None, Some("uses"), ModelSetArray(None, langUse.name))
  )))

  val tmplLang: ModelSetEntity = ModelSetEntity(None, "TmplLang", Some(ObjType(None, None, langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("content"), langFullBlock)
  )))

  val langImpl: ModelSetEntity = ModelSetEntity(None, "LangImpl", None, None, Some(List(

  )))

}
