package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.ast.common.value.{TLangDouble, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc, LangFuncParam}
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangFor, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import tlang.core.Null

object LangModel {

  val pkg = "tlang.tmpl.lang"

  val langResource: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "LangResource"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("rootDir"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("fromRoot"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("pkg"), ModelSetType(Null.empty(), TLangString.getType)),
    ModelSetAttribute(Null.empty(), Some("name"), ModelSetType(Null.empty(), TLangString.getType)),
  )))

  val langContext: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "LangContext"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("resource"), langResource),
    ModelSetAttribute(Null.empty(), Some("line"), ModelSetType(Null.empty(), TLangDouble.getType)),
    ModelSetAttribute(Null.empty(), Some("charPos"), ModelSetType(Null.empty(), TLangDouble.getType)),

  )))

  val langNode: ModelSetEntity = ModelSetEntity(Null.empty(), ManualType(pkg, "LangNode"), None, None, Some(List(
    ModelSetAttribute(Null.empty(), Some("context"), langContext)
  )))

  val getAll: List[ModelSetEntity] = List(
    LangBlock.model,
    LangFullBlock.model,
    LangPkg.model,
    LangUse.model,
    LangContent.model,
    LangFunc.model,
    LangReturn.model,
    LangEntityValue.model,
    LangExprBlock.model,
    LangImpl.model,
    LangOperation.model,
    LangCallObj.model,
    LangCallArray.model,
    LangCallFunc.model,
    LangCallFuncParam.model,
    LangCallObj.model,
    LangCallObjectLink.model,
    LangCallVar.model,
    LangAnnotationParam.model,
    LangAnonFunc.model,
    LangFunc.model,
    LangFuncParam.model,
    LangDoWhile.model,
    LangFor.model,
    LangWhile.model,
    LangArrayValue.model,
    LangBoolValue.model,
    LangDoubleValue.model,
    LangEntityValue.model,
    LangLongValue.model,
    LangStringValue.model,
    LangTextValue.model,
    LangAffect.model,
    LangAnnotation.model,
    LangAttribute.model,
    LangContent.model,
    LangExprBlock.model,
    LangIf.model,
    LangInclude.model,
    LangMultiValue.model,
    LangReturn.model,
    LangSpecialBlock.model,
    LangVar.model,
    LangVal.model,
    LangGeneric.model,
    LangType.model,
    LangSetAttribute.model,
    LangParam.model,
    LangProp.model,
  )

}
