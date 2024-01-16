package dev.tlang.tlang.tmpl.lang.ast

import dev.tlang.tlang.ast.common.value.{TLangDouble, TLangString}
import dev.tlang.tlang.ast.model.set.{ModelSetAttribute, ModelSetEntity, ModelSetType}
import dev.tlang.tlang.tmpl.lang.ast.call._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc, LangFunc, LangFuncParam}
import dev.tlang.tlang.tmpl.lang.ast.loop.{LangDoWhile, LangFor, LangWhile}
import dev.tlang.tlang.tmpl.lang.ast.primitive._

object LangModel {

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
    LangGeneric.model
  )

}
