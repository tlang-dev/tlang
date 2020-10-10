// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangModel.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link TLangModelParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface TLangModelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelBlock(TLangModelParser.ModelBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelContent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelContent(TLangModelParser.ModelContentContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelNewEntity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelNewEntity(TLangModelParser.ModelNewEntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelNewEntityValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelNewEntityValue(TLangModelParser.ModelNewEntityValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelValueType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelValueType(TLangModelParser.ModelValueTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelTbl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelTbl(TLangModelParser.ModelTblContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelEntityAsAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelEntityAsAttribute(TLangModelParser.ModelEntityAsAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelAttribute(TLangModelParser.ModelAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetEntity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetEntity(TLangModelParser.ModelSetEntityContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetAttribute(TLangModelParser.ModelSetAttributeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetValueType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetValueType(TLangModelParser.ModelSetValueTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetType(TLangModelParser.ModelSetTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelGeneric}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelGeneric(TLangModelParser.ModelGenericContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetFuncDef(TLangModelParser.ModelSetFuncDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link TLangModelParser#modelSetRef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModelSetRef(TLangModelParser.ModelSetRefContext ctx);
}