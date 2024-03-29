package dev.tlang.tlang.astbuilder

import dev.tlang.tlang.TLangParser.{TmplExpressionContext, _}
import dev.tlang.tlang.ast.tmpl._
import dev.tlang.tlang.ast.tmpl.call._
import dev.tlang.tlang.ast.tmpl.condition.TmplOperation
import dev.tlang.tlang.ast.tmpl.func.{TmplFunc, TmplFuncCurry}
import dev.tlang.tlang.ast.tmpl.loop.ForType.ForType
import dev.tlang.tlang.ast.tmpl.loop.{ForType, TmplFor}
import dev.tlang.tlang.ast.tmpl.primitive._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.ContextResource

import scala.jdk.CollectionConverters._

object BuildTmplBlock {

  def build(resource: ContextResource, tmpl: TmplBlockContext): TmplBlock = {
    if (tmpl.block.tmplFullBlock() != null) buildFullBlock(resource, tmpl, tmpl.block.tmplFullBlock())
    else buildSpecialisedBlock(resource, tmpl, tmpl.block.tmplSpecialisedBlock())
  }

  def buildFullBlock(resource: ContextResource, tmpl: TmplBlockContext, full: TmplFullBlockContext): TmplBlock = {
    val pkg = if (full.tmplPkg() != null && !full.tmplPkg().isEmpty) Some(buildPkg(resource, full.tmplPkg())) else None
    val uses: List[TmplUse] = buildUses(resource, full.tmplUses.asScala.toList)
    TmplBlock(addContext(resource, tmpl), tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, tmpl.params.asScala.toList)) else None,
      pkg, Some(uses), specialised = false,
      buildContents(resource, full.tmplContents.asScala.toList))
  }

  def buildSpecialisedBlock(resource: ContextResource, tmpl: TmplBlockContext, spec: TmplSpecialisedBlockContext): TmplBlock = {
    TmplBlock(addContext(resource, tmpl), tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, tmpl.params.asScala.toList)) else None,
      None, None, specialised = true,
      Some(List(buildSpecializedContent(resource, spec.content))))
  }

  def buildPkg(resource: ContextResource, pkg: TmplPkgContext): TmplPkg = {
    new TmplPkg(pkg.parts.asScala.toList.map(part => buildId(resource, part)))
  }

  def buildUses(resource: ContextResource, uses: List[TmplUseContext]): List[TmplUse] = {
    if (uses != null && uses.nonEmpty) uses.map(use => buildUse(resource, use))
    else List()
  }

  def buildUse(resource: ContextResource, use: TmplUseContext): TmplUse = {
    TmplUse(use.parts.asScala.toList.map(part => buildId(resource, part)),
      if (use.alias != null && !use.alias.isEmpty) Some(buildId(resource, use.alias)) else None)
  }

  def buildContents(resource: ContextResource, content: List[TmplContentContext]): Option[List[TmplContent[_]]] = {
    if (content.nonEmpty) Some(content.map(buildContent(resource, _)))
    else None
  }

  def buildContent(resource: ContextResource, content: TmplContentContext): TmplContent[_] = {
    content match {
      case impl@_ if impl.tmplImpl() != null => buildImpl(resource, impl.tmplImpl())
      case func@_ if func.tmplFunc() != null => buildFunc(resource, func.tmplFunc())
      case expr@_ if expr.tmplExpression() != null => buildExpression(resource, expr.tmplExpression())
    }
  }

  def buildSpecializedContent(resource: ContextResource, content: TmplSpecialisedContentContext): TmplNode[_] = {
    content match {
      case content@_ if content.tmplContent() != null => buildContent(resource, content.tmplContent())
      case attr@_ if attr.tmplAttribute() != null => buildAttribute(resource, attr.tmplAttribute())
      case setAttr@_ if setAttr.tmplSetAttribute() != null => buildSetAttribute(resource, setAttr.tmplSetAttribute())
      case param@_ if param.tmplParam() != null => buildParam(resource, param.tmplParam())
    }
  }

  def buildImpl(resource: ContextResource, impl: TmplImplContext): TmplImpl = {
    TmplImpl(addContext(resource, impl), buildAnnotations(resource, impl.annots.asScala.toList), buildProps(resource, impl.props),
      buildId(resource, impl.name), buildFors(resource, impl.forProps, impl.forNames), buildWiths(resource, impl.withProps, impl.withNames),
      if (impl.tmplImplContents != null && !impl.tmplImplContents.isEmpty) buildContents(resource, impl.tmplImplContents.asScala.toList) else None)
  }

  def buildFors(resource: ContextResource, props: TmplPropsContext, fors: java.util.List[TmplTypeContext]): Option[TmplImplFor] = {
    if (fors != null && !fors.isEmpty)
      Some(TmplImplFor(addContext(resource, props), buildProps(resource, props), fors.asScala.toList.map(t => buildType(resource, t))))
    else None
  }

  def buildWiths(resource: ContextResource, props: TmplPropsContext, withs: java.util.List[TmplTypeContext]): Option[TmplImplWith] = {
    if (withs != null && !withs.isEmpty)
      Some(TmplImplWith(addContext(resource, props), buildProps(resource, props), withs.asScala.toList.map(t => buildType(resource, t))))
    else None
  }

  def buildFunc(resource: ContextResource, func: TmplFuncContext): TmplFunc = {
    val curries =
      if (func.curries != null && !func.curries.isEmpty) Some(func.curries.asScala.map(curry => buildFuncCurry(resource, curry)).toList)
      else None
    TmplFunc(addContext(resource, func), buildAnnotations(resource, func.annots.asScala.toList), buildProps(resource, func.props), buildId(resource, func.name), curries,
      if (func.content != null) Some(buildExprBlock(resource, func.content)) else None,
      if (func.types != null && !func.types.isEmpty) Some(func.types.asScala.toList.map(t => buildType(resource, t))) else None, buildProps(resource, func.postProps))
  }

  def buildAnnotations(resource: ContextResource, annots: List[TmplAnnotContext]): Option[List[TmplAnnotation]] = {
    if (annots.nonEmpty) Some(annots.map(annot => {
      val params = annot.annotParams.asScala.toList.map(param => TmplAnnotationParam(addContext(resource, param), buildId(resource, param.name), buildPrimitive(resource, param.value)))
      TmplAnnotation(addContext(resource, annot), buildId(resource, annot.name), if (params.nonEmpty) Some(params) else None)
    }))
    else None
  }

  def buildProps(resource: ContextResource, props: TmplPropsContext): Option[TmplProp] = {
    val elems = props.props.asScala.toList
    if (elems.nonEmpty) Some(TmplProp(addContext(resource, props), elems.map(buildId(resource, _))))
    else None
  }

  def buildFuncCurry(resource: ContextResource, curry: TmplCurryingContext): TmplFuncCurry = {
    TmplFuncCurry(addContext(resource, curry), Option(build(resource, curry.param)))
  }

  def build(resource: ContextResource, params: TmplCurryingParamContext): List[TmplParam] = {
    if (params.params != null && !params.params.isEmpty) params.params.asScala.map(param => buildParam(resource, param)).toList
    else List()
  }

  def buildParam(resource: ContextResource, param: TmplParamContext): TmplParam = {
    TmplParam(addContext(resource, param),
      buildAnnotations(resource, param.annots.asScala.toList),
      buildId(resource, param.name),
      if (param.`type` != null && !param.`type`.isEmpty) Some(buildType(resource, param.`type`)) else None)
  }

  def buildType(resource: ContextResource, `type`: TmplTypeContext): TmplType = {
    TmplType(addContext(resource, `type`), buildId(resource, `type`.`type`), buildGeneric(resource, `type`.generic), `type`.array != null)
  }

  def buildGeneric(resource: ContextResource, generic: TmplGenericContext): Option[TmplGeneric] = {
    if (generic != null && generic.types != null && !generic.types.isEmpty) Some(TmplGeneric(addContext(resource, generic), generic.types.asScala.map(t => buildType(resource, t)).toList))
    else None
  }

  def buildExprContent(resource: ContextResource, expr: TmplExprContentContext): TmplExprContent[_] = {
    expr match {
      case block@_ if expr.tmplExprBlock() != null => buildExprBlock(resource, block.tmplExprBlock())
      case exp@_ if exp.tmplExpression() != null => buildExpression(resource, expr.tmplExpression())
    }
  }

  def buildExprBlock(resource: ContextResource, block: TmplExprBlockContext): TmplExprBlock = {
    TmplExprBlock(addContext(resource, block), block.exprs.asScala.toList.map(expr => buildExpression(resource, expr)))
  }

  def buildExpression(resource: ContextResource, expr: TmplExpressionContext): TmplExpression[_] = {
    expr match {
      case tmplVar@_ if tmplVar.tmplVar() != null => buildVar(resource, tmplVar.tmplVar())
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(resource, callObj.tmplCallObj())
      case valueType@_ if valueType.tmplValueType() != null => buildValueType(resource, valueType.tmplValueType())
      case func@_ if func.tmplFunc() != null => buildFunc(resource, func.tmplFunc())
      case whileLoop@_ if whileLoop.tmplWhile() != null => BuildTmplLoop.buildWhile(resource, whileLoop.tmplWhile())
      case doWhile@_ if doWhile.tmplDoWhile() != null => BuildTmplLoop.buildDoWhile(resource, doWhile.tmplDoWhile())
      case ifStmt@_ if ifStmt.tmplIf() != null => buildIf(resource, ifStmt.tmplIf())
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case ret@_ if ret.tmplReturn() != null => buildReturn(resource, ret.tmplReturn())
      case affect@_ if affect.tmplAffect() != null => buildAffect(resource, affect.tmplAffect())
      case tmplFor@_ if tmplFor.tmplFor() != null => buildTmplFor(resource, tmplFor.tmplFor())
      case anonFunc@_ if anonFunc.tmplAnonFunc() != null => buildTmplAnonFunc(resource, anonFunc.tmplAnonFunc())
    }
  }

  def buildInclude(resource: ContextResource, include: TmplIncludeContext): TmplInclude = {
    //    include match {
    //      case incl@_ if incl.callObj() != null => TmplInclude(List(BuildHelperStatement.buildCallObject(incl.callObj())))
    //      case block@_ if block.tmplIncludeBlock() != null => TmplInclude(block.tmplIncludeBlock().calls.asScala.toList.map(BuildHelperStatement.buildCallObject))
    //    }
    TmplInclude(addContext(resource, include), include.calls.asScala.toList.map(call => BuildHelperStatement.buildCallObject(resource, call)))
  }

  def buildTmplAnonFunc(resource: ContextResource, anonFunc: TmplAnonFuncContext): TmplAnonFunc = {
    TmplAnonFunc(addContext(resource, anonFunc), buildFuncCurry(resource, anonFunc.params), buildExprContent(resource, anonFunc.content))
  }

  def buildIf(resource: ContextResource, ifStmt: TmplIfContext): TmplIf = {
    val elseBlock = if (ifStmt.elseThen != null && !ifStmt.elseThen.isEmpty) {
      ifStmt.elseThen match {
        case ifBlock@_ if ifBlock.tmplIf() != null => Some(Right(buildIf(resource, ifBlock.tmplIf())))
        case elseBl@_ if elseBl.tmplExprContent() != null => Some(Left(buildExprContent(resource, elseBl.tmplExprContent())))
      }
    } else None
    TmplIf(addContext(resource, ifStmt), buildOperation(resource, ifStmt.cond), buildExprContent(resource, ifStmt.content), elseBlock)
  }

  def buildTmplFor(resource: ContextResource, tmplFor: TmplForContext): TmplFor = {
    TmplFor(addContext(resource, tmplFor),
      buildId(resource, tmplFor.variable),
      if (tmplFor.start != null && !tmplFor.start.isEmpty) Some(buildOperation(resource, tmplFor.start)) else None,
      buildForType(tmplFor.`type`.getText), buildOperation(resource, tmplFor.array), buildExprContent(resource, tmplFor.tmplExprContent()))
  }

  def buildForType(forType: String): ForType = forType match {
    case "in" => ForType.IN
    case "to" => ForType.TO
    case "until" => ForType.UNTIL
  }

  def buildVar(resource: ContextResource, variable: TmplVarContext): TmplVar = {
    TmplVar(addContext(resource, variable), buildAnnotations(resource, variable.annots.asScala.toList), buildProps(resource, variable.props), buildId(resource, variable.name),
      if (variable.`type` != null) Some(buildType(resource, variable.`type`)) else None,
      if (variable.value != null) Some(buildOperation(resource, variable.value)) else None)
  }

  def buildReturn(resource: ContextResource, ret: TmplReturnContext): TmplReturn = {
    TmplReturn(addContext(resource, ret), buildOperation(resource, ret.call))
  }

  def buildAffect(resource: ContextResource, affect: TmplAffectContext): TmplAffect = {
    TmplAffect(addContext(resource, affect), buildCallObject(resource, affect.variable), buildOperation(resource, affect.value))
  }

  def buildCallObject(resource: ContextResource, obj: TmplCallObjContext): TmplCallObj = {
    TmplCallObj(addContext(resource, obj), buildProps(resource, obj.props), obj.objs.asScala.toList.map(obj => buildCallObjectType(resource, obj)))
  }

  def buildCallObjectType(resource: ContextResource, objType: TmplCallObjTypeContext): TmplCallObjType[_] = {
    objType match {
      case array@_ if array.tmplCallArray() != null => buildCallArray(resource, array.tmplCallArray())
      case func@_ if func.tmplCallFunc() != null => buildCallFunc(resource, func.tmplCallFunc())
      case variable@_ if variable.tmplCallVariable() != null => buildCallVar(resource, variable.tmplCallVariable())
    }
  }

  def buildCallArray(resource: ContextResource, array: TmplCallArrayContext): TmplCallArray = {
    TmplCallArray(addContext(resource, array), buildId(resource, array.name), buildOperation(resource, array.elem))
  }

  def buildCallFunc(resource: ContextResource, func: TmplCallFuncContext): TmplCallFunc = {
    TmplCallFunc(addContext(resource, func), buildId(resource, func.name), if (func.currying != null && !func.currying.isEmpty) Some(buildCallFuncCurrying(resource, func.currying.asScala.toList)) else None)
  }

  def buildCallFuncCurrying(resource: ContextResource, currying: List[TmplCurryParamsContext]): List[TmplCurryParam] = {
    currying.map(currying => buildCallFuncParams(resource, currying))
  }

  def buildCallFuncParams(resource: ContextResource, param: TmplCurryParamsContext): TmplCurryParam = {
    TmplCurryParam(addContext(resource, param), if (param.params != null && !param.params.isEmpty) Some(param.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None)
  }

  def buildSetAttribute(resource: ContextResource, param: TmplSetAttributeContext): TmplSetAttribute = {
    TmplSetAttribute(addContext(resource, param),
      buildIdOrString(resource, param.name),
      buildOperation(resource, param.value))
  }

  def buildInclSetAttribute(resource: ContextResource, attr: TmplInclSetAttributeContext): TmplNode[_] = {
    attr match {
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case attr@_ if attr.tmplSetAttribute() != null => buildSetAttribute(resource, attr.tmplSetAttribute())
    }
  }

  def buildCallVar(resource: ContextResource, variable: TmplCallVariableContext): TmplCallVar = {
    TmplCallVar(addContext(resource, variable), buildId(resource, variable.name))
  }

  def buildValueType(resource: ContextResource, valueType: TmplValueTypeContext): TmplValueType[_] = {
    valueType match {
      case callObj@_ if callObj.tmplCallObj() != null => buildCallObject(resource, callObj.tmplCallObj())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => buildPrimitive(resource, primitive.tmplPrimitiveValue())
      case multi@_ if multi.tmplMultiValue() != null => buildMultiValue(resource, multi.tmplMultiValue())
    }
  }

  def buildOperation(resource: ContextResource, block: TmplOperationContext): TmplOperation = {
    TmplOperation(addContext(resource, block),
      if (block.content != null) Right(buildExpression(resource, block.content)) else Left(buildOperation(resource, block.innerBlock)),
      if (block.op != null) Some(BuildCommon.buildOperator(block.op.getText), buildOperation(resource, block.next)) else None)
  }

  def buildInclAttribute(resource: ContextResource, attr: TmplInclAttributeContext): TmplNode[_] = {
    attr match {
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case attr@_ if attr.tmplAttribute() != null => buildAttribute(resource, attr.tmplAttribute())
    }
  }

  def buildAttribute(resource: ContextResource, attr: TmplAttributeContext): TmplAttribute = {
    TmplAttribute(addContext(resource, attr), buildOptionId(resource, attr.attr), if (attr.`type` != null) Some(buildType(resource, attr.`type`)) else None, buildOperation(resource, attr.value))
  }

  def buildMultiValue(resource: ContextResource, value: TmplMultiValueContext): TmplMultiValue = TmplMultiValue(addContext(resource, value), value.values.asScala.toList.map(value => buildValueType(resource, value)))

  def buildPrimitive(resource: ContextResource, value: TmplPrimitiveValueContext): TmplPrimitiveValue[_] = value match {
    case string@_ if string.tmplStringValue() != null => buildString(resource, string.tmplStringValue())
    case number@_ if number.tmplNumberValue() != null => buildNumber(resource, number.tmplNumberValue())
    case text@_ if text.tmplTextValue() != null => buildText(resource, text.tmplTextValue())
    case entity@_ if entity.tmplEntityValue() != null => buildEntity(resource, entity.tmplEntityValue())
    case bool@_ if bool.tmplBoolValue() != null => buildBool(resource, bool.tmplBoolValue())
    case array@_ if array.tmplArrayValue() != null => buildArray(resource, None, array.tmplArrayValue())
  }

  def buildEntity(resource: ContextResource, entity: TmplEntityValueContext): TmplEntityValue = TmplEntityValue(
    addContext(resource, entity),
    buildOptionId(resource, entity.name),
    if (entity.params != null && !entity.params.isEmpty) Some(entity.params.asScala.toList.map(param => buildInclAttribute(resource, param))) else None,
    if (entity.attrs != null && !entity.attrs.isEmpty) Some(entity.attrs.asScala.toList.map(attr => buildInclAttribute(resource, attr))) else None
  )

  def buildArray(resource: ContextResource, `type`: Option[TmplType] = None, array: TmplArrayValueContext): TmplArrayValue = {
    TmplArrayValue(addContext(resource, array), `type`,
      if (array.params != null && !array.params.isEmpty) Some(array.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None)
  }

  def buildOptionId(resource: ContextResource, id: TmplIDContext): Option[TmplID] = {
    if (id != null && !id.isEmpty) Some(buildId(resource, id))
    else None
  }

  def buildId(resource: ContextResource, id: TmplIDContext): TmplID = id match {
    case id@_ if id.ID() != null => TmplStringID(addContext(resource, id), id.ID().getSymbol.getText)
    case interId@_ if interId.tmplIntprID() != null => TmplInterpretedID(addContext(resource, id), AstBuilderUtils.getText(interId.tmplIntprID().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprID().callObj()), AstBuilderUtils.getText(interId.tmplIntprID().pos))
  }

  def buildString(resource: ContextResource, str: TmplStringContext): TmplID = str match {
    case id@_ if id.STRING() != null => TmplStringID(addContext(resource, str), AstBuilderUtils.extraString(id.STRING().getSymbol.getText))
    case interId@_ if interId.tmplIntprString() != null => TmplInterpretedID(addContext(resource, str), AstBuilderUtils.getText(interId.tmplIntprString().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprString().callObj()), AstBuilderUtils.getText(interId.tmplIntprString().pos))
  }

  def buildText(resource: ContextResource, txt: TmplTextContext): TmplID = txt match {
    case id@_ if id.TEXT() != null => TmplStringID(addContext(resource, txt), AstBuilderUtils.extraText(id.TEXT().getSymbol.getText))
    case interId@_ if interId.tmplIntprText() != null => TmplInterpretedID(addContext(resource, txt), AstBuilderUtils.getText(interId.tmplIntprText().pre), BuildHelperStatement.buildCallObject(resource, interId.tmplIntprText().callObj()), AstBuilderUtils.getText(interId.tmplIntprText().pos))
  }

  def buildString(resource: ContextResource, string: TmplStringValueContext): TmplStringValue = TmplStringValue(addContext(resource, string), buildString(resource, string.value))

  def buildNumber(resource: ContextResource, number: TmplNumberValueContext): TmplPrimitiveValue[_] = {
    val value = number.value.getText
    if (value.contains(".")) TmplDoubleValue(addContext(resource, number), value.toDouble)
    else TmplLongValue(addContext(resource, number), value.toLong)
  }

  def buildText(resource: ContextResource, text: TmplTextValueContext): TmplTextValue = TmplTextValue(addContext(resource, text), buildText(resource, text.value))

  def buildBool(resource: ContextResource, bool: TmplBoolValueContext): TmplBoolValue = TmplBoolValue(addContext(resource, bool), bool.value.getText == "true")

  def buildIdOrString(resource: ContextResource, idOrString: TmplIdOrStringContext): Option[TmplID] = {
    if (idOrString != null && idOrString.tmplID() != null) Some(buildId(resource, idOrString.tmplID()))
    else if (idOrString != null && idOrString.tmplString() != null) Some(buildString(resource, idOrString.tmplString()))
    else None
  }

}
