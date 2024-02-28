package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.ManualType
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc, LangFuncParam}
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangFor, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.tmpl.{AstModel, BuildAstTmpl}

object LangModel {

  val pkg = "tlang.tmpl.lang"

  val langResource: AstModel = AstModel(None, ManualType(pkg, "LangResource"), None, None, Some(List(
    BuildAstTmpl.createModelAttrStr(None, Some("rootDir")),
    BuildAstTmpl.createModelAttrStr(None, Some("fromRoot")),
    BuildAstTmpl.createModelAttrStr(None, Some("pkg")),
    BuildAstTmpl.createModelAttrStr(None, Some("name")),
  )))

  val langContext: AstModel = AstModel(None, ManualType(pkg, "LangContext"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("resource"), langResource.getType),
    BuildAstTmpl.createModelAttrDouble(None, Some("line")),
    BuildAstTmpl.createModelAttrDouble(None, Some("charPos")),

  )))

  val langNode: AstModel = AstModel(None, ManualType(pkg, "LangNode"), None, None, Some(List(
    BuildAstTmpl.createModelAttrEntity(None, Some("context"), langContext.getType)
  )))

  val getAll: List[AstModel] = List(
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
