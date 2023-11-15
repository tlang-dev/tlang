package dev.tlang.tlang.astbuilder.tmpl.lang

import dev.tlang.tlang.TLangParser
import dev.tlang.tlang.TLangParser._
import dev.tlang.tlang.ast.common.ObjType
import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value.{ArrayValue, ComplexAttribute, EntityValue, TLangString}
import dev.tlang.tlang.astbuilder.BuildAst
import dev.tlang.tlang.astbuilder.BuildAst.addContext
import dev.tlang.tlang.astbuilder.context.{ContextContent, ContextResource}

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.CollectionHasAsScala

object BuildLang {

  def buildLangBlock(resource: ContextResource, block: TmplLangContext): EntityValue = {
    val context = addContext(resource, block)
    EntityValue(context, Some(ObjType(context, None, TmplLangAst.tmplLang.name)), Some(List(
      ComplexAttribute(context, Some("content"),
        Some(ObjType(context, None, TmplLangAst.langFullBlock.name)), Operation(context, None, Right(buildFullBlock(resource, block.tmplFullBlock()))))
    )))
  }

  def buildFullBlock(resource: ContextResource, full: TmplFullBlockContext): EntityValue = {
    val elems = ListBuffer.empty[ComplexAttribute]
    val context = addContext(resource, full)

    if (full.tmplPkg() != null && !full.tmplPkg().isEmpty) elems += ComplexAttribute(context, Some("package"),
      Some(ObjType(context, None, TmplLangAst.langFullBlock.name)), Operation(context, None, Right(buildPkg(resource, full.tmplPkg()))))

    elems += createArray(addContext(resource, full), "uses", buildUses(resource, full.tmplUses.asScala.toList))
    elems += createArray(addContext(resource, full), "contents", buildContents(resource, full.tmplContents.asScala.toList))

    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langFullBlock.name)),
      Some(elems.toList))
  }

  def buildPkg(resource: ContextResource, pkg: TLangParser.TmplPkgContext): EntityValue = {
    val context = addContext(resource, pkg)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langPkg.name)),
      Some(List(
        ComplexAttribute(context, Some("parts"),
          None, Operation(context, None, Right(ArrayValue(context, Some(pkg.parts.asScala.toList.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.getText)))))))))
        ))
      ))
  }

  def buildUses(resource: ContextResource, uses: List[TmplUseContext]): List[EntityValue] = {
    if (uses != null && uses.nonEmpty) uses.map(use => buildUse(resource, use))
    else List()
  }

  def buildUse(resource: ContextResource, use: TmplUseContext): EntityValue = {
    //    TmplUse(addContext(resource, use), use.parts.asScala.toList.map(part => buildId(resource, part)),
    //      if (use.alias != null && !use.alias.isEmpty) Some(buildId(resource, use.alias)) else None)
    val context = addContext(resource, use)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.langUse.name)),
      Some(List(
        createArray(context, "parts", use.parts.asScala.toList.map(part => BuildLangValue.buildId(resource, part)))
      )
      ))
  }

  def createAttrStr(context: Option[ContextContent], name: String, value: String): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangString(context, value))
    ))
  }

  def createAttrEntity(context: Option[ContextContent], name: String, value: EntityValue): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(value)
    ))
  }

  def createArray(context: Option[ContextContent], name: String, values: List[EntityValue]): ComplexAttribute = {
    ComplexAttribute(context, Some(name),
      None, Operation(context, None, Right(ArrayValue(context, Some(values.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value))))))))
    )
  }

  def buildContents(resource: ContextResource, content: List[TmplContentContext]): List[EntityValue] = {
    if (content.nonEmpty) content.map(buildContent(resource, _))
    else List()
  }

  def buildContent(resource: ContextResource, content: TmplContentContext): EntityValue = {
    content match {
      case impl@_ if impl.tmplImpl() != null => BuildLangImpl.buildImpl(resource, impl.tmplImpl())
      case func@_ if func.tmplFunc() != null => BuildLangFunc.buildFunc(resource, func.tmplFunc())
      case spec@_ if spec.tmplSpecialBlock() != null => buildSpecialBlock(resource, spec.tmplSpecialBlock())
      case expr@_ if expr.tmplExpression() != null => buildExpression(resource, expr.tmplExpression())
    }
  }

  def buildSpecialBlock(resource: ContextResource, block: TmplSpecialBlockContext): EntityValue = {
    val context = addContext(resource, block)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.tmplSpecialBlock.name)),
      Some(List(
        //        ComplexAttribute(context, Some("parts"),
        //          None, Operation(context, None, Right(ArrayValue(context, Some(pkg.parts.asScala.toList.map(part => ComplexAttribute(context, None, None, Operation(context, None, Right(new TLangString(context, part.getText)))))))))
        //        )
      )
      ))
  }

  def buildExpression(resource: ContextResource, expr: TmplExpressionContext): EntityValue = {
    expr match {
      case tmplVar@_ if tmplVar.tmplVar() != null => BuildLangValue.buildVar(resource, tmplVar.tmplVar())
      case callObj@_ if callObj.tmplCallObj() != null => BuildLangCall.buildCallObj(resource, callObj.tmplCallObj())
      case valueType@_ if valueType.tmplValueType() != null => BuildLangValue.buildValueType(resource, valueType.tmplValueType())
      case func@_ if func.tmplFunc() != null => BuildLangFunc.buildFunc(resource, func.tmplFunc())
      case whileLoop@_ if whileLoop.tmplWhile() != null => BuildLangLoop.buildWhile(resource, whileLoop.tmplWhile())
      case doWhile@_ if doWhile.tmplDoWhile() != null => BuildLangLoop.buildDoWhile(resource, doWhile.tmplDoWhile())
      case ifStmt@_ if ifStmt.tmplIf() != null => buildIf(resource, ifStmt.tmplIf())
      case incl@_ if incl.tmplInclude() != null => buildInclude(resource, incl.tmplInclude())
      case ret@_ if ret.tmplReturn() != null => buildReturn(resource, ret.tmplReturn())
      case affect@_ if affect.tmplAffect() != null => BuildLangValue.buildAffect(resource, affect.tmplAffect())
      case tmplFor@_ if tmplFor.tmplFor() != null => BuildLangLoop.buildFor(resource, tmplFor.tmplFor())
      case anonFunc@_ if anonFunc.tmplAnonFunc() != null => buildAnonFunc(resource, anonFunc.tmplAnonFunc())
      case primitive@_ if primitive.tmplPrimitiveValue() != null => BuildLangValue.buildPrimitive(resource, primitive.tmplPrimitiveValue())
      case spec@_ if spec.tmplSpecialBlock() != null => buildSpecialBlock(resource, spec.tmplSpecialBlock())
    }
  }

  def buildIf(resource: ContextResource, func: TmplIfContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.tmplIf.name)),
      Some(List(

      ))
    )
  }

  def buildInclude(resource: ContextResource, func: TmplIncludeContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.tmplInclude.name)),
      Some(List(

      ))
    )
  }

  def buildReturn(resource: ContextResource, func: TmplReturnContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplLangAst.tmplReturn.name)),
      Some(List(

      ))
    )
  }

  def buildAnonFunc(resource: ContextResource, func: TmplAnonFuncContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, TmplFuncAst.langAnonFunc.name)),
      Some(List(

      ))
    )
  }

  def buildOperation(resource: ContextResource, op: TmplOperationContext): EntityValue = {
    val context = BuildAst.addContext(resource, op)
    EntityValue(context,
      Some(ObjType(context, None, TmplExprAst.tmplOperation.name)),
      Some(List(

      ))
    )
  }

}
