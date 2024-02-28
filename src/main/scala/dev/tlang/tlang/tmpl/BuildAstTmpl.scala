package dev.tlang.tlang.tmpl

import dev.tlang.tlang.ast.common.value._
import tlang.core
import tlang.core.Type
import tlang.internal.{ContextContent, TmplID}

object BuildAstTmpl {

  /* def buildLangBlock(resource: ContextResource, block: TmplLangContext): EntityValue = {
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
   }*/

  /*  def buildPkg(resource: ContextResource, pkg: TLang.TmplPkgContext): EntityValue = {
      val context = addContext(resource, pkg)
      EntityValue(context,
        Some(ObjType(context, None, LangPkg.name)),
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
        Some(ObjType(context, None, LangUse.name)),
        Some(List(
          createArray(context, "parts", use.parts.asScala.toList.map(part => BuildLangValue.buildId(resource, part)))
        )
        ))
    }*/

  def createAttrStr(context: Option[ContextContent], name: String, value: String): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.String.TYPE), Some(new TLangString(None, value)))
  }

  def createAttrTLangStr(context: Option[ContextContent], name: String, value: TLangString): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.String.TYPE), Some(value))
  }

  //  def createAttrInt(context: Option[ContextContent], name: String, value: TLang): AstEntityAttr = {
  //    AstEntityAttr(context, Some(name), None, Some(value))
  //  }

  def createAttrLong(context: Option[ContextContent], name: String, value: TLangLong): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Long.TYPE), Some(value))
  }

  def createAttrDouble(context: Option[ContextContent], name: String, value: TLangDouble): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Double.TYPE), Some(value))
  }

  def createAttrBool(context: Option[ContextContent], name: String, value: TLangBool): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Bool.TYPE), Some(value))
  }

  def createAttrList(context: Option[ContextContent], name: String, value: List[AstValue]): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Null.TYPE),
      Some(AstListValue(context, value)))
  }

  def createAttrNull(context: Option[ContextContent], name: String, value: Option[AstValue]): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Null.TYPE), value)
  }

  def createAttrNullList(context: Option[ContextContent], name: String, value: Option[List[AstValue]]): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Null.TYPE),
      if (value.isDefined) Some(AstListValue(context, value.get)) else None)
  }

  def createAttrEntity(context: Option[ContextContent], name: String, `type`: Option[Type], value: AstEntity): AstEntityAttr = {
    AstEntityAttr(context, Some(name), `type`, Some(value))
  }

  def createArray(context: Option[ContextContent], name: String, values: ArrayValue): AstEntityAttr = {
    AstEntityAttr(context, Some(name), Some(core.Array.TYPE), Some(values))
  }

  def createModelAttrStr(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.String.TYPE), None)
  }

  def createModelAttrInt(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Int.TYPE), None)
  }

  def createModelAttrLong(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Long.TYPE), None)
  }

  def createModelAttrDouble(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Double.TYPE), None)
  }

  def createModelAttrFloat(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Float.TYPE), None)
  }

  def createModelAttrBool(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Bool.TYPE), None)
  }

  def createModelAttrArray(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Array.TYPE), None)
  }

  def createModelAttrEntity(context: Option[ContextContent], name: Option[String], `type`: Type): AstEntityAttr = {
    AstEntityAttr(context, name, Some(`type`), None)
  }

  def createModelAttrNull(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(core.Null.TYPE), None)
  }

  def createModelAttrTmplID(context: Option[ContextContent], name: Option[String]): AstEntityAttr = {
    AstEntityAttr(context, name, Some(TmplID.TYPE), None)
  }

  /*def buildContents(resource: ContextResource, content: List[TmplContentContext]): List[EntityValue] = {
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
      Some(ObjType(context, None, LangSpecialBlock.name)),
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
      Some(ObjType(context, None, LangIf.name)),
      Some(List(

      ))
    )
  }

  def buildInclude(resource: ContextResource, func: TmplIncludeContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, LangInclude.name)),
      Some(List(

      ))
    )
  }

  def buildReturn(resource: ContextResource, func: TmplReturnContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, LangReturn.name)),
      Some(List(

      ))
    )
  }

  def buildAnonFunc(resource: ContextResource, func: TmplAnonFuncContext): EntityValue = {
    val context = BuildAst.addContext(resource, func)
    EntityValue(context,
      Some(ObjType(context, None, LangAnonFunc.name)),
      Some(List(

      ))
    )
  }

  def buildOperation(resource: ContextResource, op: TmplOperationContext): EntityValue = {
    val context = BuildAst.addContext(resource, op)
    EntityValue(context,
      Some(ObjType(context, None, LangOperation.name)),
      Some(List(

      ))
    )
  }*/

}
