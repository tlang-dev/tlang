package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.value.{TLangDouble, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetArray, ModelSetAttribute, ModelSetEntity, ModelSetType}
import TmplFuncAst.tmplFuncParam

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

  val tmplSpecialBlock: ModelSetEntity = ModelSetEntity(None, "TmplSpecialBlock", Some(ObjType(None, None, langNode.name)), None, Some(List(
    ModelSetAttribute(None, Some("type"), ModelSetType(None, TLangString.getType)),
    ModelSetAttribute(None, Some("curries"), ModelSetArray(None, tmplFuncParam.name))
  )))

  val tmplAnnot: ModelSetEntity = ModelSetEntity(None, "TmplAnnot", Some(ObjType(None, None, langNode.name)), None, Some(List(

  )))

  val tmplAnnotParam: ModelSetEntity = ModelSetEntity(None, "TmplAnnotParam", Some(ObjType(None, None, langNode.name)), None, Some(List(

  )))

  val tmplIf: ModelSetEntity = ModelSetEntity(None, "TmplIf", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplInclude: ModelSetEntity = ModelSetEntity(None, "TmplInclude", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplAttribute: ModelSetEntity = ModelSetEntity(None, "TmplAttribute", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplSetAttribute: ModelSetEntity = ModelSetEntity(None, "TmplSetAttribute", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplParam: ModelSetEntity = ModelSetEntity(None, "TmplParam", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplProp: ModelSetEntity = ModelSetEntity(None, "TmplProp", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplReturn: ModelSetEntity = ModelSetEntity(None, "TmplReturn", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

  val tmplGeneric: ModelSetEntity = ModelSetEntity(None, "TmplGeneric", Some(ObjType(None, None, langNode.name)), None, Some(List(
  )))

}
