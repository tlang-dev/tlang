package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.TLang._
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder._
import dev.tlang.tlang.tmpl.common.ast.NativeType
import dev.tlang.tlang.tmpl.common.astbuilder.BuildCommonTmpl
import dev.tlang.tlang.tmpl.doc.astbuilder.BuildDoc
import dev.tlang.tlang.tmpl.lang.ast
import dev.tlang.tlang.tmpl.lang.ast._
import dev.tlang.tlang.tmpl.lang.ast.condition.LangOperation
import dev.tlang.tlang.tmpl.lang.ast.func.{LangAnnotationParam, LangAnonFunc}
import dev.tlang.tlang.tmpl.lang.ast.loop.ForType.ForType
import dev.tlang.tlang.tmpl.lang.ast.loop.{ForType, LangFor}
import dev.tlang.tlang.tmpl.lang.ast.primitive._
import dev.tlang.tlang.tmpl.style.astbuilder.BuildStyle
import tlang.internal.{AnyTmplBlock, ContextResource, TmplNode}
import tlang.{core, mutable}

import scala.jdk.CollectionConverters._

object BuildTmplBlock {

  def build(resource: ContextResource, tmpl: TmplBlockContext): AnyTmplBlock[_] = {

    tmpl match {
      case lang@_ if lang.tmplLang() != null => buildLangBlock(resource, tmpl.tmplLang())
      case doc@_ if doc.tmplDoc() != null => BuildDoc.buildTmplDoc(resource, tmpl.tmplDoc())
      case style@_ if style.tmplStyle() != null => BuildStyle.buildStyle(resource, style.tmplStyle())
      //      case data@_ if data.tmplData() != null => EntityValue(None, None)
      //      case cmd@_ if cmd.tmplCmd() != null => EntityValue(None, None)
    }


  }

  def buildLangBlock(resource: ContextResource, tmpl: TmplLangContext): LangBlock = {
    val langs = new mutable.List[core.String]()
    tmpl.langs.asScala.foreach(str => langs.add(new core.String(str.getText)))
    val content = buildFullBlock(resource, tmpl.tmplFullBlock())
    LangBlock(addContext(resource, tmpl), tmpl.name.getText, langs.toArray.get().get().getElement
      ,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, tmpl.params.asScala.toList).map(param => NativeType(param.context, param))) else None,
      content)
  }

  /* def build(resource: ContextResource, tmpl: TmplBlockContext): TmplBlock = {
     if (tmpl.block.tmplFullBlock() != null) buildFullBlock(resource, tmpl, tmpl.block.tmplFullBlock())
     else buildSpecialisedBlock(resource, tmpl, tmpl.block.tmplSpecialisedBlock())
   }*/

  def buildFullBlock(resource: ContextResource, full: TmplFullBlockContext): LangFullBlock = {
    val pkg = if (full.tmplPkg() != null && !full.tmplPkg().isEmpty) Some(buildPkg(resource, full.tmplPkg())) else None
    val uses: List[LangUse] = buildUses(resource, full.tmplUses.asScala.toList)
    LangFullBlock(addContext(resource, full),
      pkg, Some(uses), specialised = false,
      buildContents(resource, full.tmplContents.asScala.toList))
  }

  /*def buildSpecialisedBlock(resource: ContextResource, tmpl: TmplBlockContext, spec: TmplSpecialisedBlockContext): LangBlock = {
    TmplBlock(addContext(resource, tmpl), tmpl.name.getText, tmpl.lang.getText,
      if (tmpl.params != null && !tmpl.params.isEmpty) Some(BuildHelperBlock.buildParams(resource, tmpl.params.asScala.toList)) else None,
      None, None, specialised = true,
      Some(List(buildSpecializedContent(resource, spec.content))))
  }*/

  def buildPkg(resource: ContextResource, pkg: TmplPkgContext): LangPkg = {
    LangPkg(addContext(resource, pkg), pkg.parts.asScala.toList.map(part => BuildCommonTmpl.buildId(resource, part)))
  }

  def buildUses(resource: ContextResource, uses: List[TmplUseContext]): List[LangUse] = {
    if (uses != null && uses.nonEmpty) uses.map(use => buildUse(resource, use))
    else List()
  }

  def buildUse(resource: ContextResource, use: TmplUseContext): LangUse = {
    LangUse(addContext(resource, use), use.parts.asScala.toList.map(part => BuildCommonTmpl.buildId(resource, part)),
      if (use.alias != null && !use.alias.isEmpty) Some(BuildCommonTmpl.buildId(resource, use.alias)) else None)
  }

  def buildContents(resource: ContextResource, content: List[TmplContentContext]): Option[List[LangContent[_]]] = {
    if (content.nonEmpty) Some(content.map(buildContent(resource, _)))
    else None
  }

  def buildContent(resource: ContextResource, content: TmplContentContext): LangContent[_] = {
    content match {
      case impl@_ if impl.tmplImpl() != null => buildImpl(resource, impl.tmplImpl())
      case func@_ if func.tmplFunc() != null => BuildTmplFunc.buildFunc(resource, func.tmplFunc())
      case spec@_ if spec.tmplSpecialBlock() != null => buildSpecialBlock(resource, spec.tmplSpecialBlock())
      case expr@_ if expr.tmplExpression() != null => buildExpression(resource, expr.tmplExpression())
    }
  }

  def buildSpecialBlock(resource: ContextResource, block: TmplSpecialBlockContext): LangSpecialBlock = {
    val curries =
      if (block.curries != null && !block.curries.isEmpty) Some(block.curries.asScala.map(curry => BuildTmplFunc.buildFuncParam(resource, curry)).toList)
      else None
    val content = if (block.expr != null) Some(buildExprContent(resource, block.expr)) else None
    LangSpecialBlock(
      addContext(resource, block),
      block.`type`.getText,
      curries = curries,
      content = content
    )
  }

  def buildSpecializedContent(resource: ContextResource, content: TmplSpecialisedContentContext): TmplNode[_] = {
    content match {
      case content@_ if content.tmplContent() != null => buildContent(resource, content.tmplContent())
      case attr@_ if attr.tmplAttribute() != null => buildAttribute(resource, attr.tmplAttribute())
      case setAttr@_ if setAttr.tmplSetAttribute() != null => buildSetAttribute(resource, setAttr.tmplSetAttribute())
      case param@_ if param.tmplParam() != null => buildParam(resource, param.tmplParam())
    }
  }

  def buildImpl(resource: ContextResource, impl: TmplImplContext): LangImpl = {
    LangImpl(addContext(resource, impl), buildAnnotations(resource, impl.annots.asScala.toList), buildProps(resource, impl.props),
      BuildCommonTmpl.buildId(resource, impl.name), buildFors(resource, impl.forProps, impl.forNames), buildWiths(resource, impl.withProps, impl.withNames),
      if (impl.tmplImplContents != null && !impl.tmplImplContents.isEmpty) buildContents(resource, impl.tmplImplContents.asScala.toList) else None)
  }

  def buildFors(resource: ContextResource, props: TmplPropsContext, fors: java.util.List[TmplTypeContext]): Option[LangImplFor] = {
    if (fors != null && !fors.isEmpty)
      Some(LangImplFor(addContext(resource, props), buildProps(resource, props), fors.asScala.toList.map(t => buildType(resource, t))))
    else None
  }

  def buildWiths(resource: ContextResource, props: TmplPropsContext, withs: java.util.List[TmplTypeContext]): Option[LangImplWith] = {
    if (withs != null && !withs.isEmpty)
      Some(LangImplWith(addContext(resource, props), buildProps(resource, props), withs.asScala.toList.map(t => buildType(resource, t))))
    else None
  }


  def buildAnnotations(resource: ContextResource, annots: List[TmplAnnotContext]): Option[List[LangAnnotation]] = {
    if (annots.nonEmpty) Some(annots.map(annot => {
      val params = annot.annotParams.asScala.toList.map(param => LangAnnotationParam(addContext(resource, param), BuildCommonTmpl.buildOptionId(resource, param.name), buildValueType(resource, param.value)))
      LangAnnotation(addContext(resource, annot), BuildCommonTmpl.buildId(resource, annot.name), if (params.nonEmpty) Some(params) else None)
    }))
    else None
  }

  def buildProps(resource: ContextResource, props: TmplPropsContext): Option[LangProp] = {
    val elems = props.props.asScala.toList
    if (elems.nonEmpty) Some(LangProp(addContext(resource, props), elems.map(BuildCommonTmpl.buildId(resource, _))))
    else None
  }



  /*def buildCurryParam(resource: ContextResource, param: TmplCurryingParamTypeContext):TmplCurryParamType = {
    TmplCurryParam(addContext(resource, param),
      buildCurryParamType(resource, param)
    )
  }*/


  def buildParam(resource: ContextResource, param: TmplParamContext): LangParam = {
    LangParam(addContext(resource, param),
      buildAnnotations(resource, param.annots.asScala.toList),
      BuildCommonTmpl.buildId(resource, param.name),
      if (param.`type` != null && !param.`type`.isEmpty) Some(buildType(resource, param.`type`)) else None)
  }

  def buildType(resource: ContextResource, `type`: TmplTypeContext): LangType = {
    ast.LangType(addContext(resource, `type`), BuildCommonTmpl.buildId(resource, `type`.`type`), buildGeneric(resource, `type`.generic), `type`.array != null,
      if (`type`.currying != null && !`type`.currying.isEmpty) Some(BuildTmplCall.buildCallFuncCurrying(resource, `type`.currying.asScala.toList)) else None)
  }

  def buildGeneric(resource: ContextResource, generic: TmplGenericContext): Option[LangGeneric] = {
    if (generic != null && generic.types != null && !generic.types.isEmpty) Some(LangGeneric(addContext(resource, generic), generic.types.asScala.map(t => buildType(resource, t)).toList))
    else None
  }

  def buildExprContent(resource: ContextResource, expr: TmplExprContentContext): LangExprContent[_] = {
    expr match {
      case block@_ if expr.tmplExprBlock() != null => buildExprBlock(resource, block.tmplExprBlock())
      case exp@_ if exp.tmplExpression() != null => buildExpression(resource, expr.tmplExpression())
    }
  }

  def buildExprBlock(resource: ContextResource, block: TmplExprBlockContext): LangExprBlock = {
    LangExprBlock(addContext(resource, block), block.exprs.asScala.toList.map(expr => buildExpression(resource, expr)))
  }

  def buildExpression(resource: ContextResource, expr: TmplExpressionContext): LangExpression[_] = {
    expr match {
      case tmplVar@_ if tmplVar.tmplVar() != null => buildVar(resource, tmplVar.tmplVar())
      case callObj@_ if callObj.tmplCallObj() != null => BuildTmplCall.buildCallObject(resource, callObj.tmplCallObj())
      case valueType@_ if valueType.tmplValueType() != null => buildValueType(resource, valueType.tmplValueType())
      case func@_ if func.tmplFunc() != null => BuildTmplFunc.buildFunc(resource, func.tmplFunc())
      case whileLoop@_ if whileLoop.tmplWhile() != null => BuildTmplLoop.buildWhile(resource, whileLoop.tmplWhile())
      case doWhile@_ if doWhile.tmplDoWhile() != null => BuildTmplLoop.buildDoWhile(resource, doWhile.tmplDoWhile())
      case ifStmt@_ if ifStmt.tmplIf() != null => buildIf(resource, ifStmt.tmplIf())
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case ret@_ if ret.tmplReturn() != null => buildReturn(resource, ret.tmplReturn())
      case affect@_ if affect.tmplAffect() != null => buildAffect(resource, affect.tmplAffect())
      case tmplFor@_ if tmplFor.tmplFor() != null => buildTmplFor(resource, tmplFor.tmplFor())
      case anonFunc@_ if anonFunc.tmplAnonFunc() != null => buildTmplAnonFunc(resource, anonFunc.tmplAnonFunc())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => buildPrimitive(resource, primitive.tmplPrimitiveValue())
      case spec@_ if spec.tmplSpecialBlock() != null => buildSpecialBlock(resource, spec.tmplSpecialBlock())
    }
  }

  def buildInclude(resource: ContextResource, include: TmplIncludeContext): LangInclude = {
    //    include match {
    //      case incl@_ if incl.callObj() != null => TmplInclude(List(BuildHelperStatement.buildCallObject(incl.callObj())))
    //      case block@_ if block.tmplIncludeBlock() != null => TmplInclude(block.tmplIncludeBlock().calls.asScala.toList.map(BuildHelperStatement.buildCallObject))
    //    }
    LangInclude(addContext(resource, include), include.calls.asScala.toList.map(call => BuildHelperStatement.buildCallObject(resource, call)))
  }

  def buildTmplAnonFunc(resource: ContextResource, anonFunc: TmplAnonFuncContext): LangAnonFunc = {
    LangAnonFunc(addContext(resource, anonFunc), Some(List(BuildTmplFunc.buildFuncParam(resource, anonFunc.params))), buildExprContent(resource, anonFunc.content))
  }

  def buildIf(resource: ContextResource, ifStmt: TmplIfContext): LangIf = {
    val elseBlock = if (ifStmt.elseThen != null && !ifStmt.elseThen.isEmpty) {
      ifStmt.elseThen match {
        case ifBlock@_ if ifBlock.tmplIf() != null => Some(Right(buildIf(resource, ifBlock.tmplIf())))
        case elseBl@_ if elseBl.tmplExprContent() != null => Some(Left(buildExprContent(resource, elseBl.tmplExprContent())))
      }
    } else None
    LangIf(addContext(resource, ifStmt), buildOperation(resource, ifStmt.cond), buildExprContent(resource, ifStmt.content), elseBlock)
  }

  def buildTmplFor(resource: ContextResource, tmplFor: TmplForContext): LangFor = {
    LangFor(addContext(resource, tmplFor),
      BuildCommonTmpl.buildId(resource, tmplFor.variable),
      if (tmplFor.start != null && !tmplFor.start.isEmpty) Some(buildOperation(resource, tmplFor.start)) else None,
      buildForType(tmplFor.`type`.getText), buildOperation(resource, tmplFor.array), buildExprContent(resource, tmplFor.tmplExprContent()))
  }

  def buildForType(forType: String): ForType = forType match {
    case "in" => ForType.IN
    case "to" => ForType.TO
    case "until" => ForType.UNTIL
  }

  def buildVar(resource: ContextResource, variable: TmplVarContext): LangVar = {
    LangVar(addContext(resource, variable), buildAnnotations(resource, variable.annots.asScala.toList), buildProps(resource, variable.props), BuildCommonTmpl.buildId(resource, variable.name),
      if (variable.`type` != null) Some(buildType(resource, variable.`type`)) else None,
      if (variable.value != null) Some(buildOperation(resource, variable.value)) else None,
      variable.optional != null
    )
  }

  def buildReturn(resource: ContextResource, ret: TmplReturnContext): LangReturn = {
    LangReturn(addContext(resource, ret), buildOperation(resource, ret.call))
  }

  def buildAffect(resource: ContextResource, affect: TmplAffectContext): LangAffect = {
    ast.LangAffect(addContext(resource, affect), BuildTmplCall.buildCallObject(resource, affect.variable), buildOperation(resource, affect.value))
  }

  def buildSetAttribute(resource: ContextResource, param: TmplSetAttributeContext): LangSetAttribute = {
    LangSetAttribute(addContext(resource, param),
      BuildCommonTmpl.buildIdOrString(resource, param.name),
      buildOperation(resource, param.value))
  }

  def buildInclSetAttribute(resource: ContextResource, attr: TmplInclSetAttributeContext): TmplNode[_] = {
    attr match {
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case attr@_ if attr.tmplSetAttribute() != null => buildSetAttribute(resource, attr.tmplSetAttribute())
    }
  }

  def buildValueType(resource: ContextResource, valueType: TmplValueTypeContext): LangValueType[_] = {
    valueType match {
      case callObj@_ if callObj.tmplCallObj() != null => BuildTmplCall.buildCallObject(resource, callObj.tmplCallObj())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => buildPrimitive(resource, primitive.tmplPrimitiveValue())
      case multi@_ if multi.tmplMultiValue() != null => buildMultiValue(resource, multi.tmplMultiValue())
    }
  }

  def buildOperation(resource: ContextResource, block: TmplOperationContext): LangOperation = {
    LangOperation(addContext(resource, block),
      if (block.content != null) Right(buildExpression(resource, block.content)) else Left(buildOperation(resource, block.innerBlock)),
      if (block.op != null) Some(BuildCommon.buildOperator(block.op.getText), buildOperation(resource, block.next)) else None)
  }

  def buildInclAttribute(resource: ContextResource, attr: TmplInclAttributeContext): TmplNode[_] = {
    attr match {
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case attr@_ if attr.tmplAttribute() != null => buildAttribute(resource, attr.tmplAttribute())
    }
  }

  def buildAttribute(resource: ContextResource, attr: TmplAttributeContext): LangAttribute = {
    LangAttribute(addContext(resource, attr), BuildCommonTmpl.buildOptionId(resource, attr.attr), if (attr.`type` != null) Some(buildType(resource, attr.`type`)) else None, buildOperation(resource, attr.value))
  }

  def buildMultiValue(resource: ContextResource, value: TmplMultiValueContext): LangMultiValue = LangMultiValue(addContext(resource, value), value.values.asScala.toList.map(value => buildValueType(resource, value)))

  def buildPrimitive(resource: ContextResource, value: TmplPrimitiveValueContext): LangPrimitiveValue[_] = value match {
    case string@_ if string.tmplStringValue() != null => BuildCommonTmpl.buildString(resource, string.tmplStringValue())
    case number@_ if number.tmplNumberValue() != null => BuildCommonTmpl.buildNumber(resource, number.tmplNumberValue())
    case text@_ if text.tmplTextValue() != null => BuildCommonTmpl.buildText(resource, text.tmplTextValue())
    case entity@_ if entity.tmplEntityValue() != null => buildEntity(resource, entity.tmplEntityValue())
    case bool@_ if bool.tmplBoolValue() != null => BuildCommonTmpl.buildBool(resource, bool.tmplBoolValue())
    case array@_ if array.tmplArrayValue() != null => buildArray(resource, None, array.tmplArrayValue())
  }

  def buildEntity(resource: ContextResource, entity: TmplEntityValueContext): LangEntityValue = LangEntityValue(
    addContext(resource, entity),
    BuildCommonTmpl.buildOptionId(resource, entity.name),
    if (entity.params != null && !entity.params.isEmpty) Some(entity.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None,
    if (entity.attrs != null && !entity.attrs.isEmpty) Some(entity.attrs.asScala.toList.map(attr => buildInclSetAttribute(resource, attr))) else None
  )

  def buildArray(resource: ContextResource, `type`: Option[LangType] = None, array: TmplArrayValueContext): LangArrayValue = {
    LangArrayValue(addContext(resource, array), `type`,
      if (array.params != null && !array.params.isEmpty) Some(array.params.asScala.toList.map(param => buildInclSetAttribute(resource, param))) else None)
  }

}
