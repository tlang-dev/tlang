// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangHelper.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TLangHelperParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TLangHelperVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperBlock(TLangHelperParser.HelperBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperFunc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperFunc(TLangHelperParser.HelperFuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCurrying}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCurrying(TLangHelperParser.HelperCurryingContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperParam(TLangHelperParser.HelperParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperParamType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperParamType(TLangHelperParser.HelperParamTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperObjType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperObjType(TLangHelperParser.HelperObjTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperArrayType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperArrayType(TLangHelperParser.HelperArrayTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperFuncType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperFuncType(TLangHelperParser.HelperFuncTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperContent(TLangHelperParser.HelperContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperStatement(TLangHelperParser.HelperStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperIf}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperIf(TLangHelperParser.HelperIfContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperElse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperElse(TLangHelperParser.HelperElseContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperConditionBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperConditionBlock(TLangHelperParser.HelperConditionBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCondition(TLangHelperParser.HelperConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#conditionMark}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionMark(TLangHelperParser.ConditionMarkContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperFor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperFor(TLangHelperParser.HelperForContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallObj}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallObj(TLangHelperParser.HelperCallObjContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallObjType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallObjType(TLangHelperParser.HelperCallObjTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallString}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallString(TLangHelperParser.HelperCallStringContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallNumber}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallNumber(TLangHelperParser.HelperCallNumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallText}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallText(TLangHelperParser.HelperCallTextContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallArray}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallArray(TLangHelperParser.HelperCallArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallFunc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallFunc(TLangHelperParser.HelperCallFuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallVariable}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallVariable(TLangHelperParser.HelperCallVariableContext ctx);
}