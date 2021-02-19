package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser.{TmplExpressionContext, _}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.{TmplCondition, TmplConditionBlock}
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl.primitive._

import scala.jdk.CollectionConverters._

object BuildTmplBlock {

  def build(tmpl: TmplBlockContext): TmplBlock = {
    val pkg = if (tmpl.tmplPkg() != null && !tmpl.tmplPkg().isEmpty) Some(buildPkg(tmpl.tmplPkg())) else None
    val uses: List[TmplUse] = buildUses(tmpl.tmplUses.asScala.toList)
    TmplBlock(tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(BuildHelperBlock.buildParams(tmpl.params.asScala.toList)) else None,
      pkg, Some(uses),
      buildContent(tmpl.tmplContents.asScala.toList)
    )
  }

  def buildPkg(pkg: TmplPkgContext): TmplPkg = {
    new TmplPkg(pkg.parts.asScala.toList.map(buildId))
  }

  def buildUses(uses: List[TmplUseContext]): List[TmplUse] = {
    if (uses != null && uses.nonEmpty) uses.map(buildUse)
    else List()
  }

  def buildUse(use: TmplUseContext): TmplUse = {
    TmplUse(use.parts.asScala.toList.map(buildId))
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
    TmplImpl(buildAnnotations(impl.annots.asScala.toList), buildProps(impl.props),
      buildId(impl.name), buildFors(impl.forNames), buildWiths(impl.withNames), if (impl.tmplImplContents != null && !impl.tmplImplContents.isEmpty) buildContent(impl.tmplImplContents.asScala.toList) else None)
  }

  def buildFors(fors: java.util.List[TmplIDContext]): Option[List[TmplImplFor]] = {
    if (fors != null && !fors.isEmpty) Some(fors.asScala.toList.map(id => TmplImplFor(buildId(id))))
    else None
  }

  def buildWiths(withs: java.util.List[TmplIDContext]): Option[List[TmplImplWith]] = {
    if (withs != null && !withs.isEmpty) Some(withs.asScala.toList.map(id => TmplImplWith(buildId(id))))
    else None
  }

  def buildFunc(func: TmplFuncContext): TmplFunc = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) Some(func.curries.asScala.map(build).toList)
      else None
    TmplFunc(buildAnnotations(func.annots.asScala.toList), buildProps(func.props), buildId(func.name), curries,
      if (func.content != null) Some(buildExprBlock(func.content)) else None,
      if (func.types != null && !func.types.isEmpty) Some(func.types.asScala.toList.map(buildType)) else None)
  }

  def buildAnnotations(annots: List[TmplAnnotContext]): Option[List[TmplAnnotation]] = {
    if (annots.nonEmpty) Some(annots.map(annot => {
      val params = annot.annotParams.asScala.toList.map(param => TmplAnnotationParam(param.name.getText, buildPrimitive(param.value)))
      TmplAnnotation(annot.name.getText, if (params.nonEmpty) Some(params) else None)
    }))
    else None
  }

  def buildProps(props: TmplPropsContext): Option[TmplProp] = {
    val elems = props.props.asScala.toList
    if (elems.nonEmpty) Some(TmplProp(elems.map(_.getText)))
    else None
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
    TmplType(buildId(`type`.`type`), buildGeneric(`type`.generic), `type`.array != null)
  }

  def buildGeneric(generic: TmplGenericContext): Option[TmplGeneric] = {
    if (generic != null && generic.types != null && !generic.types.isEmpty) Some(TmplGeneric(generic.types.asScala.map(buildType).toList))
    else None
  }

  def buildExprContent(expr: TmplExprContentContext): TmplExprContent = {
    expr match {
      case block@_ if expr.tmplExprBlock() != null => buildExprBlock(block.tmplExprBlock())
      case exp@_ if exp.tmplExpression() != null => buildExpression(expr.tmplExpression())
    }
  }

  def buildExprBlock(block: TmplExprBlockContext): TmplExprBlock = {
    TmplExprBlock(block.exprs.asScala.toList.map(buildExpression))
  }

  def buildExpression(expr: TmplExpressionContext): TmplExpression = {
    expr match {
      case tmplVar@_ if tmplVar.tmplVar() != null => buildVar(tmplVar.tmplVar())
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(callObj.tmplCallObj())
      case valueType@_ if valueType.tmplValueType() != null => buildValueType(valueType.tmplValueType())
      case cond@_ if cond.tmplConditionBlock() != null => buildConditionBlock(cond.tmplConditionBlock())
      case func@_ if func.tmplFunc() != null => buildFunc(func.tmplFunc())
      case whileLoop@_ if whileLoop.tmplWhile() != null => BuildTmplLoop.buildWhile(whileLoop.tmplWhile())
      case doWhile@_ if doWhile.tmplDoWhile() != null => BuildTmplLoop.buildDoWhile(doWhile.tmplDoWhile())
      case ifStmt@_ if ifStmt.tmplIf() != null => buildIf(ifStmt.tmplIf())
      case incl@_ if incl.tmplInclude() != null => buildInclude(incl.tmplInclude())
    }
  }

  def buildInclude(include: TmplIncludeContext): TmplInclude = {
    //    include match {
    //      case incl@_ if incl.callObj() != null => TmplInclude(List(BuildHelperStatement.buildCallObject(incl.callObj())))
    //      case block@_ if block.tmplIncludeBlock() != null => TmplInclude(block.tmplIncludeBlock().calls.asScala.toList.map(BuildHelperStatement.buildCallObject))
    //    }
    TmplInclude(include.calls.asScala.toList.map(BuildHelperStatement.buildCallObject))
  }

  def buildIf(ifStmt: TmplIfContext): TmplIf = {
    val elseBlock = if (ifStmt.elseThen != null && !ifStmt.elseThen.isEmpty) {
      ifStmt.elseThen match {
        case ifBlock@_ if ifBlock.tmplIf() != null => Some(Right(buildIf(ifBlock.tmplIf())))
        case elseBl@_ if elseBl.tmplExprContent() != null => Some(Left(buildExprContent(elseBl.tmplExprContent())))
      }
    } else None
    TmplIf(buildConditionBlock(ifStmt.cond), buildExprContent(ifStmt.content), elseBlock)
  }

  def buildVar(variable: TmplVarContext): TmplVar = {
    TmplVar(buildAnnotations(variable.annots.asScala.toList), buildProps(variable.props), buildId(variable.name), buildType(variable.`type`), if (variable.value != null) Some(buildExpression(variable.value)) else None)
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
    TmplCallArray(buildId(array.name), buildValueType(array.elem))
  }

  def buildCallFunc(func: TmplCallFuncContext): TmplCallFunc = {
    TmplCallFunc(buildId(func.name), if (func.currying != null && !func.currying.isEmpty) Some(buildCallFuncCurrying(func.currying.asScala.toList)) else None)
  }

  def buildCallFuncCurrying(currying: List[TmplCurryParamsContext]): List[TmplCurryParam] = {
    currying.map(buildCallFuncParams)
  }

  def buildCallFuncParams(param: TmplCurryParamsContext): TmplCurryParam = {
    TmplCurryParam(if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(buildSetAttribute)) else None)
  }

  def buildSetAttribute(param: TmplSetAttributeContext): TmplSetAttribute = {
    TmplSetAttribute(buildOptionId(param.name), buildValueType(param.value))
  }

  def buildCallVar(variable: TmplCallVariableContext): TmplCallVar = {
    TmplCallVar(buildId(variable.name))
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

  def buildEitherCondition(block: TmplConditionBlockContext): Either[TmplConditionBlock, TmplCondition] =
    if (block.innerBlock != null) Left(buildConditionBlock(block.innerBlock))
    else Right(buildCondition(block.content))

  def buildCondition(condition: TmplConditionContext): TmplCondition = TmplCondition(buildSimpleValueType(condition.arg1),
    if (condition.mark != null) Some(BuildHelperStatement.buildConditionType(condition.mark.getText)) else None,
    if (condition.arg2 != null) Some(buildSimpleValueType(condition.arg2)) else None,
    if (condition.link != null) Some(BuildHelperStatement.buildConditionLink(condition.link)) else None,
    if (condition.next != null) Some(buildConditionBlock(condition.next)) else None)

  def buildAttribute(attr: TmplAttributeContext): TmplAttribute = {
    TmplAttribute(buildOptionId(attr.attr), if (attr.`type` != null) Some(buildType(attr.`type`)) else None, buildValueType(attr.value))
  }

  def buildMultiValue(value: TmplMultiValueContext): TmplMultiValue = TmplMultiValue(value.values.asScala.toList.map(buildValueType))

  def buildPrimitive(value: TmplPrimitiveValueContext): TmplPrimitiveValue = value match {
    case string@_ if string.tmplStringValue() != null => buildString(string.tmplStringValue())
    case number@_ if number.tmplNumberValue() != null => buildNumber(number.tmplNumberValue())
    case text@_ if text.tmplTextValue() != null => buildText(text.tmplTextValue())
    case entity@_ if entity.tmplEntityValue() != null => buildEntity(entity.tmplEntityValue())
    case bool@_ if bool.tmplBoolValue() != null => buildBool(bool.tmplBoolValue())
    case array@_ if array.tmplArrayValue() != null => buildArray(None, array.tmplArrayValue())
  }

  def buildEntity(entity: TmplEntityValueContext): TmplEntityValue = TmplEntityValue(
    if (entity.params != null && !entity.params.isEmpty) Some(entity.params.asScala.toList.map(buildAttribute)) else None,
    if (entity.attrs != null && !entity.attrs.isEmpty) Some(entity.attrs.asScala.toList.map(buildAttribute)) else None)

  def buildArray(`type`: Option[TmplType] = None, array: TmplArrayValueContext): TmplArrayValue = {
    TmplArrayValue(`type`,
      if (array.params != null && !array.params.isEmpty) Some(array.params.asScala.toList.map(buildSetAttribute)) else None)
  }

  def buildOptionId(id: TmplIDContext): Option[TmplID] = {
    if (id != null && !id.isEmpty) Some(buildId(id))
    else None
  }

  def buildId(id: TmplIDContext): TmplID = id match {
    case id@_ if id.ID() != null => TmplStringID(id.ID().getSymbol.getText)
    case interId@_ if interId.tmplIntprID() != null => TmplInterpretedID(AstBuilderUtils.getText(interId.tmplIntprID().pre), BuildHelperStatement.buildCallObject(interId.tmplIntprID().callObj()), AstBuilderUtils.getText(interId.tmplIntprID().pos))
  }

  def buildString(str: TmplStringContext): TmplID = str match {
    case id@_ if id.STRING() != null => TmplStringID(AstBuilderUtils.extraString(id.STRING().getSymbol.getText))
    case interId@_ if interId.tmplIntprString() != null => TmplInterpretedID(AstBuilderUtils.getText(interId.tmplIntprString().pre), BuildHelperStatement.buildCallObject(interId.tmplIntprString().callObj()), AstBuilderUtils.getText(interId.tmplIntprString().pos))
  }

  def buildText(txt: TmplTextContext): TmplID = txt match {
    case id@_ if id.TEXT() != null => TmplStringID(AstBuilderUtils.extraText(id.TEXT().getSymbol.getText))
    case interId@_ if interId.tmplIntprText() != null => TmplInterpretedID(AstBuilderUtils.getText(interId.tmplIntprText().pre), BuildHelperStatement.buildCallObject(interId.tmplIntprText().callObj()), AstBuilderUtils.getText(interId.tmplIntprText().pos))
  }

  def buildString(string: TmplStringValueContext): TmplStringValue = TmplStringValue(buildString(string.value))

  def buildNumber(number: TmplNumberValueContext): TmplPrimitiveValue = {
    val value = number.value.getText
    if (value.contains(".")) TmplDoubleValue(value.toDouble)
    else TmplLongValue(value.toLong)
  }

  def buildText(text: TmplTextValueContext): TmplTextValue = TmplTextValue(buildText(text.value))

  def buildBool(bool: TmplBoolValueContext): TmplBoolValue = TmplBoolValue(bool.value.getText == "true")

}
