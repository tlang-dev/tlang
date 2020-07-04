// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TLangParser}.
 */
public interface TLangListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TLangParser#domainmodel}.
	 * @param ctx the parse tree
	 */
	void enterDomainmodel(TLangParser.DomainmodelContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangParser#domainmodel}.
	 * @param ctx the parse tree
	 */
	void exitDomainmodel(TLangParser.DomainmodelContext ctx);
}