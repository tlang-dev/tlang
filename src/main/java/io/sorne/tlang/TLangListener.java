// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
package io.sorne.tlang;
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
}