grammar TLangTmpl;

import CommonLexer;

	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock:
	'tmpl' '[' lang=ID ']' name=ID ('('params+=ID (',' params+=ID)*')')? '{'
	(tmplPakage=tmplPkg)?
	(tmplUses+=tmplUse)*
	(tmplContents+=tmplContent)*
	'}';

tmplContent: tmplImpl | tmplFunc | tmplExpression;

tmplPkg: 'pkg' parts+=ID ('.' parts+=ID)*;

tmplUse: 'use' parts+=ID ('.' parts+=ID)*;

tmplAnnot: '@' name=ID ('(' annotParams+=tmplAnnotParam (',' annotParams+=tmplAnnotParam)* ')')?;

tmplAnnotParam:name=ID '=' value=tmplPrimitiveValue;

tmplProps: ('[' (props+=ID)+ ']')?;

tmplImpl:
    (annots+=tmplAnnot)*
	'impl' props=tmplProps  name=ID (('for' forProps=tmplProps  forNames+=ID) (',' forNames+=ID)* (('with' withProps=tmplProps  withNames+=ID) (',' withNames+=ID)*)?)? '{'
	(tmplImplContents+=tmplContent)*
	'}';

tmplFunc:
    (annots+=tmplAnnot)*
	'func' props=tmplProps name=ID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? (':' ret=tmplMultiValue)?('{'
	    exprs+=tmplExpression*
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

tmplExpression:	tmplVar | tmplCallObj | tmplValueType | tmplConditionBlock | tmplFunc;

tmplVar:
    (annots+=tmplAnnot)*
    'var' name=ID (':' type=tmplType)? ('=' value=tmplExpression)?;

tmplCallObj: objs+=tmplCallObjType ('.'objs+=tmplCallObjType)*;

tmplCallObjType: tmplCallArray | tmplCallFunc | tmplCallVariable;

tmplCallFunc: ((name=ID) | '_') (currying += tmplCurryParams)+;

tmplCurryParams:'(' (params+=tmplSetAttribute (',' params+=tmplSetAttribute)*)? ')';

tmplSetAttribute: (name=ID '=')? value=tmplValueType;

tmplCallArray: name=ID '[' elem=tmplValueType ']';

tmplCallVariable: name=ID;

tmplValueType: tmplCallObj | tmplPrimitiveValue | tmplConditionBlock | tmplMultiValue;

tmplSimpleValueType: tmplCallObj | tmplPrimitiveValue;

tmplPrimitiveValue: tmplStringValue | tmplNumberValue | tmplTextValue | tmplEntityValue | tmplBoolValue | tmplArrayValue;

tmplStringValue: value=STRING;

tmplNumberValue: value=NUMBER;

tmplTextValue: value=TEXT;

tmplBoolValue: value= 'true' | 'false';

tmplArrayValue: '[' (params+=tmplSetAttribute)? (',' params+=tmplSetAttribute)* ']';

tmplAttribute: ((attr=ID)? (':' type=tmplType)? value=tmplValueType);

tmplMultiValue: '(' (values+=tmplValueType) (',' values+=tmplValueType)* ')';

tmplEntityValue:
	 ('(' ((params+=tmplAttribute) (',' params+=tmplAttribute)*) ')')? '{'
	attrs+=tmplAttribute*
	'}';

tmplConditionBlock:
     content=tmplCondition (link=('&&' | '||')  next=tmplConditionBlock)* |
    '(' content=tmplCondition ')' (link=('&&' | '||')  next=tmplConditionBlock)* |
    '(' content=tmplCondition (link=('&&' | '||')  next=tmplConditionBlock)* ')' |
    '(' innerBlock=tmplConditionBlock ')' (link=('&&' | '||')  next=tmplConditionBlock)*
;

tmplCondition:
    arg1=tmplSimpleValueType (mark=tmplConditionMark arg2=tmplSimpleValueType)? (link=('&&' | '||') next=tmplConditionBlock)*
;

tmplConditionMark: '==' | '!=' | '<' | '>' | '<=' | '>=';
