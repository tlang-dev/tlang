// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TLangParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TLangVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TLangParser#domainModel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDomainModel(TLangParser.DomainModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#lang}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLang(TLangParser.LangContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(TLangParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#helperBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperBlock(TLangParser.HelperBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#helperFunc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperFunc(TLangParser.HelperFuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplBlock(TLangParser.TmplBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplPkg}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplPkg(TLangParser.TmplPkgContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplUse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplUse(TLangParser.TmplUseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplImpl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplImpl(TLangParser.TmplImplContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplImplContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplImplContent(TLangParser.TmplImplContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplFunc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplFunc(TLangParser.TmplFuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplCurrying}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplCurrying(TLangParser.TmplCurryingContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplCurryingParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplCurryingParam(TLangParser.TmplCurryingParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplParam(TLangParser.TmplParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplType(TLangParser.TmplTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplGeneric}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplGeneric(TLangParser.TmplGenericContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplExpression(TLangParser.TmplExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplVal}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplVal(TLangParser.TmplValContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#tmplVar}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTmplVar(TLangParser.TmplVarContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelBlock(TLangParser.ModelBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelContent(TLangParser.ModelContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelNewEntity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelNewEntity(TLangParser.ModelNewEntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelValueType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelValueType(TLangParser.ModelValueTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelTbl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelTbl(TLangParser.ModelTblContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelEntityAsAttribut}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelEntityAsAttribut(TLangParser.ModelEntityAsAttributContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelAttribut}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelAttribut(TLangParser.ModelAttributContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetEntity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetEntity(TLangParser.ModelSetEntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetAttribute(TLangParser.ModelSetAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetValueType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetValueType(TLangParser.ModelSetValueTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetType(TLangParser.ModelSetTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelGeneric}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelGeneric(TLangParser.ModelGenericContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetFuncDef(TLangParser.ModelSetFuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangParser#modelSetRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetRef(TLangParser.ModelSetRefContext ctx);
}