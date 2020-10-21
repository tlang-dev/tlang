grammar TLang;

import TLangModel, TLangHelper, CommonLexer;


/*
 * Domain Model
 * This is the entry point of the language
 *
 */
domainModel: body+=domainBlock*;

domainBlock: helperBlock | tmplBlock | modelBlock;

	/*
 * Defines the language for the tl file
 */

//HELPER: 'helper';
//FUNC: 'func';

	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock:
	'tmpl' '{'
	('lang' lang=STRING)
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
	'impl' name=ID (('for' forName=ID) (',' forNames+=ID)*)? '{'
	(tmplImplContents+=tmplImplContent)*
	'}';

tmplImplContent:
    tmplExpression | tmplFunc;

tmplFunc:
	'func' name=ID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? ('{' exprs+=tmplExpression*
	'}')?;

tmplCurrying:
	'(' param=tmplCurryingParam ')';

tmplCurryingParam:
	((params+=tmplParam) (',' params+=tmplParam)*)?;

tmplParam:
	accessor=ID? name=ID (':' type=tmplType)?;

tmplType:
	type=ID ('<' (generic=tmplGeneric) '>')? (array='[' ']')?;

tmplGeneric:
	(types+=tmplType (',' types+=tmplType)*);

tmplExpression:
	tmplVal | tmplVar;

tmplVal:
	'val' name=ID (':' type=tmplType)? ('=' value=tmplExpression)?;

tmplVar:
	'var' name=ID (':' type=tmplType)? ('=' value=tmplExpression)?;
