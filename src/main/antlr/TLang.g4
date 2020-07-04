grammar TLang;

/*
 * Domain Model
 * This is the entry point of the language
 *
 */
domainmodel:
	lang?
	file?
	/*(helperBlocks+=HelperBlock)*
	(tmplBlocks+=TmplBlock)*
	(modelBlocks+=ModelBlock)*
	*/	;


WS : ( ' ' | '\t' | '\r' | '\n' )+ -> channel(HIDDEN);
fragment ESCAPED_QUOTE : '\\"';
STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

NUMBER     : '0'..'9'+ ('.' '0'..'9'+)?;

	/*
 * Defines the language for the tl file
 */
lang: 'lang' name=STRING;

	/*
 * Defines the destination file where the results will be written
 * Only tl files containing this statement will generate a file
 */
file:
	'file' name=STRING;

	/*
 * Helper block
 * The helper is interpreted and therefore, offers dynamic results for the template
 */
/*HelperBlock:
	{HelperBlock} 'helper' '{'
	(helperFuncs+=HelperFunc)*
	'}';

HelperFunc:
	'func' name=AnyID '{'
	'}';

	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
/*TmplBlock:
	{TmplBlock} 'tmpl' '{'
	(tmplPkg=TmplPkg)?
	(tmplUses+=TmplUse)*
	(tmplImpls+=TmplImpl)*
	(tmplFuncs+=TmplFunc)*
	'}';

TmplPkg:
	'pkg' name=STRING;

TmplUse:
	'use' name=STRING;

TmplImpl:
	'impl' name=AnyID (('for' forName=AnyID) (',' forNames+=AnyID)*)? '{'
	(tmplExprs+=TmplExpression)*
	(tmplFuncs+=TmplFunc)*
	'}';

TmplFunc:
	'func' name=AnyID curries+=TmplCurrying* (':' types+=TmplType (',' types+=TmplType)*)? ('{' exprs+=TmplExpression*
	'}')?;

TmplCurrying:
	'(' params+=TmplCurryingParam ')';

TmplCurryingParam:
	{TmplCurryingParam} ((params+=TmplParam) (',' params+=TmplParam)*)?;

TmplParam:
	accessor=ID? name=AnyID (':' type=AnyID)?;

TmplType:
	type=ID ('<' (generic=TmplGeneric) '>')? (array='[' ']')?;

TmplGeneric:
	(types+=TmplType (',' types+=TmplType)*);

TmplExpression:
	TmplVal | TmplVar;

TmplVal:
	'val' name=AnyID (':' type=TmplType)? ('=' value=TmplExpression)?;

TmplVar:
	'var' name=AnyID (':' type=TmplType)? ('=' value=TmplExpression)?;

	/*
 * Model Block
 * Set the data model and the entities to personalize the generated template
 */
/*ModelBlock:
	{ModelBlock} 'model' '{'
	modelEntities+=ModelNewEntity*
	'}';

ModelNewEntity:
	type=ID ('(' ((attrs+=ModelAttribut) (',' attrs+=ModelAttribut)*) ')')? '{'
	decl+=ModelValueType*
	'}';

ModelValueType:
	(ModelAttribut | ModelEntityAsAttribut | ModelTbl);

ModelTbl:
	attr=ID? value=('[')
	((elms+=ModelValueType) (',' elms+=ModelValueType)*)
	']';

ModelEntityAsAttribut:
	(attr=ID? value=ModelNewEntity);

ModelAttribut:
	(attr=ID? value=STRING);*/

TEXT:
	'"""' '"""';

ANY_ID:
	ID | ID_RPL;

ID:
	'^'? ('a'..'z' | 'A'..'Z' | '_' | '-') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')*;
ID_RPL:
	'^'? ('a'..'z' | 'A'..'Z' | '_' | '-' | '${') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-' | '${' | '}')*;



//ML_COMMENT : '/*' -> '*/';
//SL_COMMENT : '//' !('\n'|'\r')* ('\r'? '\n')?;

//WS         : (' '|'\t'|'\r'|'\n')+ -> skip;
//WS     : [ \t\r\n]+ -> channel(HIDDEN);
