package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.model.set.ModelSetEntity

object TmplValueAst {

  val langMultiValue: ModelSetEntity = ModelSetEntity(None, "LangMultiValue", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langValueType: ModelSetEntity = ModelSetEntity(None, "LangValueType", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langVal: ModelSetEntity = ModelSetEntity(None, "LangVal", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langVar: ModelSetEntity = ModelSetEntity(None, "LangVar", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langAffect: ModelSetEntity = ModelSetEntity(None, "LangAffect", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langEntity: ModelSetEntity = ModelSetEntity(None, "LangEntity", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langString: ModelSetEntity = ModelSetEntity(None, "LangString", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langText: ModelSetEntity = ModelSetEntity(None, "LangText", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langBool: ModelSetEntity = ModelSetEntity(None, "LangBool", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langArray: ModelSetEntity = ModelSetEntity(None, "LangArray", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langLong: ModelSetEntity = ModelSetEntity(None, "LangLong", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langDouble: ModelSetEntity = ModelSetEntity(None, "LangDouble", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langStringId: ModelSetEntity = ModelSetEntity(None, "LangStringId", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langInterpretedId: ModelSetEntity = ModelSetEntity(None, "LangInterpretedId", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langReplacedId: ModelSetEntity = ModelSetEntity(None, "LangReplacedId", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

  val langBlockId: ModelSetEntity = ModelSetEntity(None, "LangBlockId", Some(ObjType(None, None, TmplLangAst.langNode.name)), None, Some(List(
  )))

}
