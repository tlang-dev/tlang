package io.sorne.tlang
import org.antlr.v4.runtime.tree.{ErrorNode, ParseTree, RuleNode, TerminalNode}

class TLangInterpreterVisitor extends TLangVisitor[String] {
  /**
   * Visit a parse tree produced by {@link TLangParser#domainmodel}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitDomainModel(ctx: TLangParser.DomainModelContext): String = {
    "toto"
  }

  override def visit(tree: ParseTree): String = {
    //tree.
    ""
  }

  override def visitChildren(node: RuleNode): String = {
    ""
  }

  override def visitTerminal(node: TerminalNode): String = {
    ""
  }

  override def visitErrorNode(node: ErrorNode): String = {
    ""
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#lang}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitLang(ctx: TLangParser.LangContext): String = {
//    val a: Int = ctx.start.getStartIndex
//    val b: Int = ctx.stop.getStopIndex
//    val interval = new Interval(a, b)
//    ctx.start.getInputStream.getText(interval)
//    ctx.name.getText
    ctx.children.forEach(x => {println(x.getText)})
    ctx.name.getText
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#file}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitFile(ctx: TLangParser.FileContext): String = {
    ctx.name.getText
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#helperBlock}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitHelperBlock(ctx: TLangParser.HelperBlockContext): String = {
    null
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#helperFunc}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitHelperFunc(ctx: TLangParser.HelperFuncContext): String = {
null
  }

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplBlock}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplBlock(ctx: TLangParser.TmplBlockContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplPkg}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplPkg(ctx: TLangParser.TmplPkgContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplUse}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplUse(ctx: TLangParser.TmplUseContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplImpl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplImpl(ctx: TLangParser.TmplImplContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplFunc}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplFunc(ctx: TLangParser.TmplFuncContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplCurrying}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplCurrying(ctx: TLangParser.TmplCurryingContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplCurryingParam}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplCurryingParam(ctx: TLangParser.TmplCurryingParamContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplParam}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplParam(ctx: TLangParser.TmplParamContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplType}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplType(ctx: TLangParser.TmplTypeContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplGeneric}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplGeneric(ctx: TLangParser.TmplGenericContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplExpression}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplExpression(ctx: TLangParser.TmplExpressionContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplVal}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplVal(ctx: TLangParser.TmplValContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplVar}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplVar(ctx: TLangParser.TmplVarContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelBlock}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelBlock(ctx: TLangParser.ModelBlockContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelNewEntity}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelNewEntity(ctx: TLangParser.ModelNewEntityContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelValueType}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelValueType(ctx: TLangParser.ModelValueTypeContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelTbl}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelTbl(ctx: TLangParser.ModelTblContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelEntityAsAttribut}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelEntityAsAttribut(ctx: TLangParser.ModelEntityAsAttributContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#modelAttribut}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitModelAttribut(ctx: TLangParser.ModelAttributContext): String = ???

  /**
   * Visit a parse tree produced by {@link TLangParser#tmplImplContent}.
   *
   * @param ctx the parse tree
   * @return the visitor result
   */
  override def visitTmplImplContent(ctx: TLangParser.TmplImplContentContext): String = ???
}
