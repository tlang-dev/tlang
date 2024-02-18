package dev.tlang.tlang.tmpl.lang.astbuilder

import dev.tlang.tlang.ast.common.operation.Operation
import dev.tlang.tlang.ast.common.value._
import tlang.core.{Bool, Null, Value}
import tlang.internal.{ContextContent, TmplNode}
import tlang.{Entity, core}

object BuildLang {

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

  def createAttrStr(context: Null[ContextContent], name: String, value: String): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangString(context, value))
    ))
  }

  def createAttrInt(context: Null[ContextContent], name: String, value: Int): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangLong(context, new core.Long(value)))
    ))
  }

  def createAttrLong(context: Null[ContextContent], name: String, value: Long): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangLong(context, new core.Long(value)))
    ))
  }

  def createAttrDouble(context: Null[ContextContent], name: String, value: Double): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangDouble(context, new core.Double(value)))
    ))
  }

  def createAttrBool(context: Null[ContextContent], name: String, value: Boolean): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new TLangBool(context, new Bool(value)))
    ))
  }

  def createAttrNull(context: Null[ContextContent], name: String, value: Option[TmplNode], valueType: Option[TLangType]): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(new NullValue[Value[_]](context,
        if(value.isDefined) Null.of(value.get.toEntity) else Null.empty(),
        valueType))
    ))
  }

  def createAttrEntity(context: Null[ContextContent], name: String, value: Entity): ComplexAttribute = {
    ComplexAttribute(context, Some(name), None, Operation(
      context, None, Right(value)
    ))
  }

  def createArray(context: Null[ContextContent], name: String, values: List[Entity]): ComplexAttribute = {
    ComplexAttribute(context, Some(name),
      None, Operation(context, None, Right(ArrayValue(context, Some(values.map(value => ComplexAttribute(context, None, None, Operation(context, None, Right(value))))))))
    )
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
