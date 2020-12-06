package io.sorne.tlang.astbuilder

import io.sorne.tlang.TLangParser.{TmplExpressionContext, _}
import io.sorne.tlang.ast.tmpl._
import io.sorne.tlang.ast.tmpl.call._
import io.sorne.tlang.ast.tmpl.condition.{TmplCondition, TmplConditionBlock}
import io.sorne.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import io.sorne.tlang.ast.tmpl.primitive._
import org.antlr.v4.runtime.Token

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

object BuildTmplBlock {

  def build(tmpl: TmplBlockContext): TmplBlock = {
    val pkg = if (tmpl.tmplPkg() != null && !tmpl.tmplPkg().isEmpty) Some(buildPkg(tmpl.tmplPkg())) else None
    val uses: List[TmplUse] = buildUses(tmpl.tmplUses.asScala.toList)
    new TmplBlock(tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(tmpl.params.asScala.toList.map(_.getText)) else None,
      pkg, Some(uses),
      buildContent(tmpl.tmplContents.asScala.toList)
    )
  }

  def buildPkg(pkg: TmplPkgContext): TmplPkg = {
    new TmplPkg(pkg.parts.asScala.toList.map(_.getText))
  }

  def buildUses(uses: List[TmplUseContext]): List[TmplUse] = {
    if (uses != null && uses.nonEmpty) uses.map(buildUse)
    else List()
  }

  def buildUse(use: TmplUseContext): TmplUse = {
    new TmplUse(use.parts.asScala.toList.map(_.getText))
  }

  def buildContent(content: List[TmplContentContext]): Option[List[TmplContent]] = {
    if (content.nonEmpty) Some(content.map {
      case impl@_ if impl.tmplImpl() != null => buildImpl(impl.tmplImpl())
      case func@_ if func.tmplFunc() != null => buildFunc(func.tmplFunc())
      case expr@_ if expr.tmplExpression() != null => buildExpression(expr.tmplExpression())
    })
    else None
  }

  def buildImpl(impl: TmplImplContext): TmplImpl = {
    TmplImpl(impl.name.getText, buildFor(impl.forName, impl.forNames), if (impl.tmplImplContents != null && !impl.tmplImplContents.isEmpty) buildContent(impl.tmplImplContents.asScala.toList) else None)
  }

  def buildFor(for1: Token, fors: java.util.List[Token]): Option[List[TmplImplFor]] = {
    val forsRet = new ListBuffer[TmplImplFor]
    if (for1 != null && for1.getText != null && for1.getText.nonEmpty) forsRet += TmplImplFor(AstBuilderUtils.extraString(for1.getText))
    if (fors != null) fors.asScala.foreach(token => forsRet += TmplImplFor(AstBuilderUtils.extraString(token.getText)))
    if (forsRet.nonEmpty) Some(forsRet.toList)
    else None
  }

  def buildFunc(func: TmplFuncContext): TmplFunc = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) Some(func.curries.asScala.map(build).toList)
      else None
    TmplFunc(func.name.getText, curries,
      if (func.exprs != null && !func.exprs.isEmpty) Some(func.exprs.asScala.toList.map(buildExpression)) else None)
  }

  def build(curry: TmplCurryingContext): TmplFuncCurry = {
    TmplFuncCurry(Option(build(curry.param)))
  }

  def build(params: TmplCurryingParamContext): List[TmplParam] = {
    if (params.params != null && !params.params.isEmpty) params.params.asScala.map(build).toList
    else List()
  }

  def build(param: TmplParamContext): TmplParam = {
    TmplParam(param.name.getText, buildType(param.`type`))
  }

  def buildType(`type`: TmplTypeContext): TmplType = {
    TmplType(`type`.`type`.getText, build(`type`.generic), `type`.array != null)
  }

  def build(generic: TmplGenericContext): Option[TmplGeneric] = {
    if (generic != null && generic.types != null && !generic.types.isEmpty) Some(TmplGeneric(generic.types.asScala.map(buildType).toList))
    else None
  }

  def buildExpression(expr: TmplExpressionContext): TmplExpression = {
    expr match {
      case tmplVar@_ if tmplVar.tmplVar() != null => buildVar(tmplVar.tmplVar())
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(callObj.tmplCallObj())
      case valueType@_ if valueType.tmplValueType() != null => buildValueType(valueType.tmplValueType())
      case cond@_ if cond.tmplConditionBlock() != null => buildConditionBlock(cond.tmplConditionBlock())
      case func@_ if func.tmplFunc() != null => buildFunc(func.tmplFunc())
    }
  }

  def buildVar(variable: TmplVarContext): TmplVar = {
    TmplVar(variable.name.getText, buildType(variable.`type`), buildExpression(variable.value))
  }

  def buildCallObject(obj: TmplCallObjContext): TmplCallObj = {
    TmplCallObj(obj.objs.asScala.toList.map(buildCallObjectType))
  }

  def buildCallObjectType(objType: TmplCallObjTypeContext): TmplCallObjType = {
    objType match {
      case array@_ if array.tmplCallArray() != null => buildCallArray(array.tmplCallArray())
      case func@_ if func.tmplCallFunc() != null => buildCallFunc(func.tmplCallFunc())
      case variable@_ if variable.tmplCallVariable() != null => buildCallVar(variable.tmplCallVariable())
    }
  }

  def buildCallArray(array: TmplCallArrayContext): TmplCallArray = {
    TmplCallArray(array.name.getText, buildValueType(array.elem))
  }

  def buildCallFunc(func: TmplCallFuncContext): TmplCallFunc = {
    TmplCallFunc(func.name.getText, if (func.currying != null && !func.currying.isEmpty) Some(buildCallFuncCurrying(func.currying.asScala.toList)) else None)
  }

  def buildCallFuncCurrying(currying: List[TmplCurryParamsContext]): List[TmplCurryParam] = {
    currying.map(buildCallFuncParams)
  }

  def buildCallFuncParams(param: TmplCurryParamsContext): TmplCurryParam = {
    TmplCurryParam(if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(buildSetAttribute)) else None)
  }

  def buildSetAttribute(param: TmplSetAttributeContext): TmplSetAttribute = {
    TmplSetAttribute(AstBuilderUtils.getText(param.name), buildValueType(param.value))
  }

  def buildCallVar(variable: TmplCallVariableContext): TmplCallVar = {
    TmplCallVar(variable.name.getText)
  }

  def buildValueType(valueType: TmplValueTypeContext): TmplValueType = {
    valueType match {
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(callObj.tmplCallObj())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => buildPrimitive(primitive.tmplPrimitiveValue())
      case cond@_ if cond.tmplConditionBlock() != null => buildConditionBlock(cond.tmplConditionBlock())
      case multi@_ if multi.tmplMultiValue() != null => buildMultiValue(multi.tmplMultiValue())
    }
  }

  def buildSimpleValueType(valueType: TmplSimpleValueTypeContext): TmplSimpleValueType = {
    valueType match {
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(callObj.tmplCallObj())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => buildPrimitive(primitive.tmplPrimitiveValue())
    }
  }

  def buildConditionBlock(block: TmplConditionBlockContext): TmplConditionBlock = {
    TmplConditionBlock(buildEitherCondition(block),
      if (block.link != null) Some(BuildHelperStatement.buildConditionLink(block.link)) else None,
      if (block.next != null) Some(buildConditionBlock(block.next)) else None)
  }

  def buildEitherCondition(block: TmplConditionBlockContext): Either[TmplConditionBlock, TmplCondition] = {
    if (block.innerBlock != null) Left(buildConditionBlock(block.innerBlock))
    else Right(buildCondition(block.content))
  }

  def buildCondition(condition: TmplConditionContext): TmplCondition = {
    TmplCondition(buildSimpleValueType(condition.arg1),
      if (condition.mark != null) Some(BuildHelperStatement.buildConditionType(condition.mark.getText)) else None,
      if (condition.arg2 != null) Some(buildSimpleValueType(condition.arg2)) else None,
      if (condition.link != null) Some(BuildHelperStatement.buildConditionLink(condition.link)) else None,
      if (condition.next != null) Some(buildConditionBlock(condition.next)) else None)
  }

  def buildAttribute(attr: TmplAttributeContext): TmplAttribute = {
    TmplAttribute(AstBuilderUtils.getText(attr.attr), if (attr.`type` != null) Some(buildType(attr.`type`)) else None, buildValueType(attr.value))
  }

  def buildMultiValue(value: TmplMultiValueContext): TmplMultiValue = {
    TmplMultiValue(value.values.asScala.toList.map(buildValueType))
  }

  def buildPrimitive(value: TmplPrimitiveValueContext): TmplPrimitiveValue = {
    value match {
      case string@_ if string.tmplStringValue() != null => buildString(string.tmplStringValue())
      case number@_ if number.tmplNumberValue() != null => buildNumber(number.tmplNumberValue())
      case text@_ if text.tmplTextValue() != null => buildText(text.tmplTextValue())
      case entity@_ if entity.tmplEntityValue() != null => buildEntity(entity.tmplEntityValue())
      case bool@_ if bool.tmplBoolValue() != null => buildBool(bool.tmplBoolValue())
      case array@_ if array.tmplArrayValue() != null => buildArray(array.tmplArrayValue())
    }
  }

  def buildEntity(entity: TmplEntityValueContext): TmplEntityValue = {
    TmplEntityValue(
      if (entity.params != null && !entity.params.isEmpty) Some(entity.params.asScala.toList.map(buildAttribute)) else None,
      if (entity.attrs != null && !entity.attrs.isEmpty) Some(entity.attrs.asScala.toList.map(buildAttribute)) else None)
  }

  def buildArray(array: TmplArrayValueContext): TmplArrayValue = {
    TmplArrayValue(if (array.params != null && !array.params.isEmpty) Some(array.params.asScala.toList.map(buildSetAttribute)) else None)
  }

  def buildString(string: TmplStringValueContext): TmplStringValue = TmplStringValue(AstBuilderUtils.extraString(string.value.getText))

  def buildNumber(number: TmplNumberValueContext): TmplPrimitiveValue = {
    val value = number.value.getText
    if (value.contains(".")) TmplDoubleValue(value.toDouble)
    else TmplLongValue(value.toLong)
  }

  def buildText(text: TmplTextValueContext): TmplTextValue = TmplTextValue(AstBuilderUtils.extraText(text.value.getText))

  def buildBool(bool: TmplBoolValueContext): TmplBoolValue = TmplBoolValue(bool.value.getText == "true")

}
