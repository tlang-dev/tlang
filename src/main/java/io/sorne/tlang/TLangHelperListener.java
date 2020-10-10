// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangHelper.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TLangHelperParser}.
 */
public interface TLangHelperListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperBlock}.
	 * @param ctx the parse tree
	 */
	void enterHelperBlock(TLangHelperParser.HelperBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperBlock}.
	 * @param ctx the parse tree
	 */
	void exitHelperBlock(TLangHelperParser.HelperBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperFunc}.
	 * @param ctx the parse tree
	 */
	void enterHelperFunc(TLangHelperParser.HelperFuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperFunc}.
	 * @param ctx the parse tree
	 */
	void exitHelperFunc(TLangHelperParser.HelperFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperStatement}.
	 * @param ctx the parse tree
	 */
	void enterHelperStatement(TLangHelperParser.HelperStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperStatement}.
	 * @param ctx the parse tree
	 */
	void exitHelperStatement(TLangHelperParser.HelperStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperIf}.
	 * @param ctx the parse tree
	 */
	void enterHelperIf(TLangHelperParser.HelperIfContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperIf}.
	 * @param ctx the parse tree
	 */
	void exitHelperIf(TLangHelperParser.HelperIfContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperCondition}.
	 * @param ctx the parse tree
	 */
	void enterHelperCondition(TLangHelperParser.HelperConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperCondition}.
	 * @param ctx the parse tree
	 */
	void exitHelperCondition(TLangHelperParser.HelperConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#conditionMark}.
	 * @param ctx the parse tree
	 */
	void enterConditionMark(TLangHelperParser.ConditionMarkContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#conditionMark}.
	 * @param ctx the parse tree
	 */
	void exitConditionMark(TLangHelperParser.ConditionMarkContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperFor}.
	 * @param ctx the parse tree
	 */
	void enterHelperFor(TLangHelperParser.HelperForContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperFor}.
	 * @param ctx the parse tree
	 */
	void exitHelperFor(TLangHelperParser.HelperForContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperCallObj}.
	 * @param ctx the parse tree
	 */
	void enterHelperCallObj(TLangHelperParser.HelperCallObjContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperCallObj}.
	 * @param ctx the parse tree
	 */
	void exitHelperCallObj(TLangHelperParser.HelperCallObjContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperCallArray}.
	 * @param ctx the parse tree
	 */
	void enterHelperCallArray(TLangHelperParser.HelperCallArrayContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperCallArray}.
	 * @param ctx the parse tree
	 */
	void exitHelperCallArray(TLangHelperParser.HelperCallArrayContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperCallFunc}.
	 * @param ctx the parse tree
	 */
	void enterHelperCallFunc(TLangHelperParser.HelperCallFuncContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperCallFunc}.
	 * @param ctx the parse tree
	 */
	void exitHelperCallFunc(TLangHelperParser.HelperCallFuncContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangHelperParser#helperCallVariable}.
	 * @param ctx the parse tree
	 */
	void enterHelperCallVariable(TLangHelperParser.HelperCallVariableContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangHelperParser#helperCallVariable}.
	 * @param ctx the parse tree
	 */
	void exitHelperCallVariable(TLangHelperParser.HelperCallVariableContext ctx);
}