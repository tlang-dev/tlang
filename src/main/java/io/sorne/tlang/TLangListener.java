// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLang.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TLangParser}.
 */
public interface TLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TLangParser#domainModel}.
	 * @param ctx the parse tree
	 */
	void enterDomainModel(TLangParser.DomainModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#domainModel}.
	 * @param ctx the parse tree
	 */
	void exitDomainModel(TLangParser.DomainModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#lang}.
	 * @param ctx the parse tree
	 */
	void enterLang(TLangParser.LangContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#lang}.
	 * @param ctx the parse tree
	 */
	void exitLang(TLangParser.LangContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(TLangParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(TLangParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplBlock}.
	 * @param ctx the parse tree
	 */
	void enterTmplBlock(TLangParser.TmplBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplBlock}.
	 * @param ctx the parse tree
	 */
	void exitTmplBlock(TLangParser.TmplBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplPkg}.
	 * @param ctx the parse tree
	 */
	void enterTmplPkg(TLangParser.TmplPkgContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplPkg}.
	 * @param ctx the parse tree
	 */
	void exitTmplPkg(TLangParser.TmplPkgContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplUse}.
	 * @param ctx the parse tree
	 */
	void enterTmplUse(TLangParser.TmplUseContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplUse}.
	 * @param ctx the parse tree
	 */
	void exitTmplUse(TLangParser.TmplUseContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplImpl}.
	 * @param ctx the parse tree
	 */
	void enterTmplImpl(TLangParser.TmplImplContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplImpl}.
	 * @param ctx the parse tree
	 */
	void exitTmplImpl(TLangParser.TmplImplContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplImplContent}.
	 * @param ctx the parse tree
	 */
	void enterTmplImplContent(TLangParser.TmplImplContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplImplContent}.
	 * @param ctx the parse tree
	 */
	void exitTmplImplContent(TLangParser.TmplImplContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplFunc}.
	 * @param ctx the parse tree
	 */
	void enterTmplFunc(TLangParser.TmplFuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplFunc}.
	 * @param ctx the parse tree
	 */
	void exitTmplFunc(TLangParser.TmplFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplCurrying}.
	 * @param ctx the parse tree
	 */
	void enterTmplCurrying(TLangParser.TmplCurryingContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplCurrying}.
	 * @param ctx the parse tree
	 */
	void exitTmplCurrying(TLangParser.TmplCurryingContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplCurryingParam}.
	 * @param ctx the parse tree
	 */
	void enterTmplCurryingParam(TLangParser.TmplCurryingParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplCurryingParam}.
	 * @param ctx the parse tree
	 */
	void exitTmplCurryingParam(TLangParser.TmplCurryingParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplParam}.
	 * @param ctx the parse tree
	 */
	void enterTmplParam(TLangParser.TmplParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplParam}.
	 * @param ctx the parse tree
	 */
	void exitTmplParam(TLangParser.TmplParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplType}.
	 * @param ctx the parse tree
	 */
	void enterTmplType(TLangParser.TmplTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplType}.
	 * @param ctx the parse tree
	 */
	void exitTmplType(TLangParser.TmplTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplGeneric}.
	 * @param ctx the parse tree
	 */
	void enterTmplGeneric(TLangParser.TmplGenericContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplGeneric}.
	 * @param ctx the parse tree
	 */
	void exitTmplGeneric(TLangParser.TmplGenericContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplExpression}.
	 * @param ctx the parse tree
	 */
	void enterTmplExpression(TLangParser.TmplExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplExpression}.
	 * @param ctx the parse tree
	 */
	void exitTmplExpression(TLangParser.TmplExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplVal}.
	 * @param ctx the parse tree
	 */
	void enterTmplVal(TLangParser.TmplValContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplVal}.
	 * @param ctx the parse tree
	 */
	void exitTmplVal(TLangParser.TmplValContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#tmplVar}.
	 * @param ctx the parse tree
	 */
	void enterTmplVar(TLangParser.TmplVarContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#tmplVar}.
	 * @param ctx the parse tree
	 */
	void exitTmplVar(TLangParser.TmplVarContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelBlock}.
	 * @param ctx the parse tree
	 */
	void enterModelBlock(TLangParser.ModelBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelBlock}.
	 * @param ctx the parse tree
	 */
	void exitModelBlock(TLangParser.ModelBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelContent}.
	 * @param ctx the parse tree
	 */
	void enterModelContent(TLangParser.ModelContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelContent}.
	 * @param ctx the parse tree
	 */
	void exitModelContent(TLangParser.ModelContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelNewEntity}.
	 * @param ctx the parse tree
	 */
	void enterModelNewEntity(TLangParser.ModelNewEntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelNewEntity}.
	 * @param ctx the parse tree
	 */
	void exitModelNewEntity(TLangParser.ModelNewEntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelValueType}.
	 * @param ctx the parse tree
	 */
	void enterModelValueType(TLangParser.ModelValueTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelValueType}.
	 * @param ctx the parse tree
	 */
	void exitModelValueType(TLangParser.ModelValueTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelTbl}.
	 * @param ctx the parse tree
	 */
	void enterModelTbl(TLangParser.ModelTblContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelTbl}.
	 * @param ctx the parse tree
	 */
	void exitModelTbl(TLangParser.ModelTblContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelEntityAsAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelEntityAsAttribute(TLangParser.ModelEntityAsAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelEntityAsAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelEntityAsAttribute(TLangParser.ModelEntityAsAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelAttribute(TLangParser.ModelAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelAttribute(TLangParser.ModelAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetEntity}.
	 * @param ctx the parse tree
	 */
	void enterModelSetEntity(TLangParser.ModelSetEntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetEntity}.
	 * @param ctx the parse tree
	 */
	void exitModelSetEntity(TLangParser.ModelSetEntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelSetAttribute(TLangParser.ModelSetAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelSetAttribute(TLangParser.ModelSetAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetValueType}.
	 * @param ctx the parse tree
	 */
	void enterModelSetValueType(TLangParser.ModelSetValueTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetValueType}.
	 * @param ctx the parse tree
	 */
	void exitModelSetValueType(TLangParser.ModelSetValueTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetType}.
	 * @param ctx the parse tree
	 */
	void enterModelSetType(TLangParser.ModelSetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetType}.
	 * @param ctx the parse tree
	 */
	void exitModelSetType(TLangParser.ModelSetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelGeneric}.
	 * @param ctx the parse tree
	 */
	void enterModelGeneric(TLangParser.ModelGenericContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelGeneric}.
	 * @param ctx the parse tree
	 */
	void exitModelGeneric(TLangParser.ModelGenericContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 */
	void enterModelSetFuncDef(TLangParser.ModelSetFuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 */
	void exitModelSetFuncDef(TLangParser.ModelSetFuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#modelSetRef}.
	 * @param ctx the parse tree
	 */
	void enterModelSetRef(TLangParser.ModelSetRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#modelSetRef}.
	 * @param ctx the parse tree
	 */
	void exitModelSetRef(TLangParser.ModelSetRefContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperBlock}.
	 * @param ctx the parse tree
	 */
	void enterHelperBlock(TLangParser.HelperBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperBlock}.
	 * @param ctx the parse tree
	 */
	void exitHelperBlock(TLangParser.HelperBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperFunc}.
	 * @param ctx the parse tree
	 */
	void enterHelperFunc(TLangParser.HelperFuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperFunc}.
	 * @param ctx the parse tree
	 */
	void exitHelperFunc(TLangParser.HelperFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperStatement}.
	 * @param ctx the parse tree
	 */
	void enterHelperStatement(TLangParser.HelperStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperStatement}.
	 * @param ctx the parse tree
	 */
	void exitHelperStatement(TLangParser.HelperStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperIf}.
	 * @param ctx the parse tree
	 */
	void enterHelperIf(TLangParser.HelperIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperIf}.
	 * @param ctx the parse tree
	 */
	void exitHelperIf(TLangParser.HelperIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperCondition}.
	 * @param ctx the parse tree
	 */
	void enterHelperCondition(TLangParser.HelperConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperCondition}.
	 * @param ctx the parse tree
	 */
	void exitHelperCondition(TLangParser.HelperConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperFor}.
	 * @param ctx the parse tree
	 */
	void enterHelperFor(TLangParser.HelperForContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperFor}.
	 * @param ctx the parse tree
	 */
	void exitHelperFor(TLangParser.HelperForContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangParser#helperCallFund}.
	 * @param ctx the parse tree
	 */
	void enterHelperCallFund(TLangParser.HelperCallFundContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#helperCallFund}.
	 * @param ctx the parse tree
	 */
	void exitHelperCallFund(TLangParser.HelperCallFundContext ctx);
}