// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangHelper.g4 by ANTLR 4.8
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
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCondition(TLangHelperParser.HelperConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperFor}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperFor(TLangHelperParser.HelperForContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangHelperParser#helperCallFund}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHelperCallFund(TLangHelperParser.HelperCallFundContext ctx);
}