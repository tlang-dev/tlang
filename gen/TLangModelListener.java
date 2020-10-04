// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLangModel.g4 by ANTLR 4.8
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link TLangModelParser}.
 */
public interface TLangModelListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelBlock}.
	 * @param ctx the parse tree
	 */
	void enterModelBlock(TLangModelParser.ModelBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelBlock}.
	 * @param ctx the parse tree
	 */
	void exitModelBlock(TLangModelParser.ModelBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelContent}.
	 * @param ctx the parse tree
	 */
	void enterModelContent(TLangModelParser.ModelContentContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelContent}.
	 * @param ctx the parse tree
	 */
	void exitModelContent(TLangModelParser.ModelContentContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelNewEntity}.
	 * @param ctx the parse tree
	 */
	void enterModelNewEntity(TLangModelParser.ModelNewEntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelNewEntity}.
	 * @param ctx the parse tree
	 */
	void exitModelNewEntity(TLangModelParser.ModelNewEntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelValueType}.
	 * @param ctx the parse tree
	 */
	void enterModelValueType(TLangModelParser.ModelValueTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelValueType}.
	 * @param ctx the parse tree
	 */
	void exitModelValueType(TLangModelParser.ModelValueTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelTbl}.
	 * @param ctx the parse tree
	 */
	void enterModelTbl(TLangModelParser.ModelTblContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelTbl}.
	 * @param ctx the parse tree
	 */
	void exitModelTbl(TLangModelParser.ModelTblContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelEntityAsAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelEntityAsAttribute(TLangModelParser.ModelEntityAsAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelEntityAsAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelEntityAsAttribute(TLangModelParser.ModelEntityAsAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelAttribute(TLangModelParser.ModelAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelAttribute(TLangModelParser.ModelAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetEntity}.
	 * @param ctx the parse tree
	 */
	void enterModelSetEntity(TLangModelParser.ModelSetEntityContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetEntity}.
	 * @param ctx the parse tree
	 */
	void exitModelSetEntity(TLangModelParser.ModelSetEntityContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 */
	void enterModelSetAttribute(TLangModelParser.ModelSetAttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetAttribute}.
	 * @param ctx the parse tree
	 */
	void exitModelSetAttribute(TLangModelParser.ModelSetAttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetValueType}.
	 * @param ctx the parse tree
	 */
	void enterModelSetValueType(TLangModelParser.ModelSetValueTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetValueType}.
	 * @param ctx the parse tree
	 */
	void exitModelSetValueType(TLangModelParser.ModelSetValueTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetType}.
	 * @param ctx the parse tree
	 */
	void enterModelSetType(TLangModelParser.ModelSetTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetType}.
	 * @param ctx the parse tree
	 */
	void exitModelSetType(TLangModelParser.ModelSetTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelGeneric}.
	 * @param ctx the parse tree
	 */
	void enterModelGeneric(TLangModelParser.ModelGenericContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelGeneric}.
	 * @param ctx the parse tree
	 */
	void exitModelGeneric(TLangModelParser.ModelGenericContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 */
	void enterModelSetFuncDef(TLangModelParser.ModelSetFuncDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetFuncDef}.
	 * @param ctx the parse tree
	 */
	void exitModelSetFuncDef(TLangModelParser.ModelSetFuncDefContext ctx);
	/**
	 * Enter a parse tree produced by {@link TLangModelParser#modelSetRef}.
	 * @param ctx the parse tree
	 */
	void enterModelSetRef(TLangModelParser.ModelSetRefContext ctx);
	/**
	 * Exit a parse tree produced by {@link TLangModelParser#modelSetRef}.
	 * @param ctx the parse tree
	 */
	void exitModelSetRef(TLangModelParser.ModelSetRefContext ctx);
}