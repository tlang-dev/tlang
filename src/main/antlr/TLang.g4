grammar TLang;

/*
 * Domain Model
 * This is the entry point of the language
 *
 */
domainModel:
	lang?
	file?
	(helperBlocks+=helperBlock)*
	(tmplBlocks+=tmplBlock)*
	(modelBlocks+=modelBlock)*
		;

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
helperBlock:
	'helper' '{'
	(helperFuncs+=helperFunc)*
	'}';

helperFunc:
	'func' name=ANY_ID '{'
	'}';


	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock:
	'tmpl' '{'
	(tmplPakage=tmplPkg)?
	(tmplUses+=tmplUse)*
	(tmplImpls+=tmplImpl)*
	(tmplFuncs+=tmplFunc)*
	'}';

tmplPkg:
	'pkg' name=STRING;

tmplUse:
	'use' name=STRING;

tmplImpl:
	'impl' name=ANY_ID (('for' forName=ANY_ID) (',' forNames+=ANY_ID)*)? '{'
	(tmplImplContents+=tmplImplContent)*
	'}';

tmplImplContent:
    tmplExpression | tmplFunc;

tmplFunc:
	'func' name=ANY_ID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? ('{' exprs+=tmplExpression*
	'}')?;

tmplCurrying:
	'(' param=tmplCurryingParam ')';

tmplCurryingParam:
	((params+=tmplParam) (',' params+=tmplParam)*)?;

tmplParam:
	accessor=ANY_ID? name=ANY_ID (':' type=tmplType)?;

tmplType:
	type=ANY_ID ('<' (generic=tmplGeneric) '>')? (array='[' ']')?;

tmplGeneric:
	(types+=tmplType (',' types+=tmplType)*);

tmplExpression:
	tmplVal | tmplVar;

tmplVal:
	'val' name=ANY_ID (':' type=tmplType)? ('=' value=tmplExpression)?;

tmplVar:
	'var' name=ANY_ID (':' type=tmplType)? ('=' value=tmplExpression)?;

	/*
 * Model Block
 * Set the data model and the entities to personalize the generated template
 */
modelBlock:
	'model' '{'
	modelEntities+=modelNewEntity*
	'}';

modelNewEntity:
	type=ID ('(' ((attrs+=modelAttribut) (',' attrs+=modelAttribut)*) ')')? '{'
	decl+=modelValueType*
	'}';

modelValueType:
	(modelAttribut | modelEntityAsAttribut | modelTbl);

modelTbl:
	attr=ID? ('[')
	((elms+=modelValueType) (',' elms+=modelValueType)*)
	']';

modelEntityAsAttribut:
	(attr=ID? value=modelNewEntity);

modelAttribut:
	(attr=ID? value=STRING);

TEXT:
	'"""' '"""';

ANY_ID:
	ID | ID_RPL;

ID:
	'^'? ('a'..'z' | 'A'..'Z' | '_' | '-') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')*;
ID_RPL:
	'^'? ('a'..'z' | 'A'..'Z' | '_' | '-' | '${') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-' | '${' | '}')*;

WS : ( ' ' | '\t' | '\r' | '\n' )+ -> channel(HIDDEN);

ESCAPED_QUOTE : '\\"';
STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

NUMBER     : '0'..'9'+ ('.' '0'..'9'+)?;



//ML_COMMENT : '/*' -> '*/';
//SL_COMMENT : '//' !('\n'|'\r')* ('\r'? '\n')?;

//WS         : (' '|'\t'|'\r'|'\n')+ -> skip;
//WS     : [ \t\r\n]+ -> channel(HIDDEN);
