grammar TLangTmpl;

import TLangCommon, TLangHelper, CommonLexer;

	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock:
	'tmpl' '[' lang=tmplID ']' name=tmplID ('('params += helperParam (',' params += helperParam)*')')? '{'
	(tmplPakage=tmplPkg)?
	(tmplUses+=tmplUse)*
	(tmplContents+=tmplContent)*
	'}';

tmplContent: tmplImpl | tmplFunc | tmplExpression;

tmplPkg: 'pkg' parts+=tmplID ('.' parts+=tmplID)*;

tmplUse: 'use' parts+=tmplID ('.' parts+=tmplID)* ('as' alias=tmplID)?;

tmplAnnot: '@' name=tmplID ('(' annotParams+=tmplAnnotParam (',' annotParams+=tmplAnnotParam)* ')')?;

tmplAnnotParam:name=tmplID '=' value=tmplPrimitiveValue;

tmplProps: ('[' (props+=tmplID)+ ']')?;

tmplImpl:
    (annots+=tmplAnnot)*
	'impl' props=tmplProps  name=tmplID (('for' forProps=tmplProps  forNames+=tmplID) (',' forNames+=tmplID)* (('with' withProps=tmplProps  withNames+=tmplID) (',' withNames+=tmplID)*)?)? '{'
	(tmplImplContents+=tmplContent)*
	'}';

tmplFunc:
    (annots+=tmplAnnot)*
	'func' props=tmplProps name=tmplID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? content=tmplExprBlock?;

tmplCurrying: '(' param=tmplCurryingParam ')';

tmplCurryingParam:
	((params+=tmplParam) (',' params+=tmplParam)*)?;

tmplParam:
	accessor=tmplID? name=tmplID (':' type=tmplType)?;

tmplType:
	type=tmplID ('<' (generic=tmplGeneric) '>')? (array='[' ']')?;

tmplGeneric:
	(types+=tmplType (',' types+=tmplType)*);

tmplExprContent: tmplExpression | tmplExprBlock;

tmplExprBlock: '{' exprs+=tmplExpression* '}';

tmplExpression:	tmplVar | tmplCallObj | tmplValueType | tmplFunc
                | tmplIf | tmplFor | tmplWhile | tmplDoWhile | tmplInclude | tmplReturn | tmplAffect;

tmplIf: 'if' '(' cond=tmplOperation ')' content=tmplExprContent elseThen=tmplElse?;

tmplElse: 'else' (tmplIf | tmplExprContent);

tmplFor: 'for' '(' var=tmplID start=tmplOperation? type=('in' | 'to' | 'until') array=tmplOperation ')' content=tmplExprContent;

tmplWhile: 'while' '(' cond=tmplOperation ')' content=tmplExprContent;

tmplDoWhile: 'do' content=tmplExprContent 'while' '(' cond=tmplOperation ')';

tmplVar:
    (annots+=tmplAnnot)*
    'var' props=tmplProps name=tmplID (':' type=tmplType)? ('=' value=tmplOperation)?;

tmplCallObj: objs+=tmplCallObjType ('.'objs+=tmplCallObjType)*;

tmplCallObjType: tmplCallArray | tmplCallFunc | tmplCallVariable;

tmplCallFunc: ((name=tmplID) | '_') (currying += tmplCurryParams)+;

tmplCurryParams:'(' (params+=tmplSetAttribute (',' params+=tmplSetAttribute)*)? ')';

tmplSetAttribute: (name=tmplID '=')? value=tmplOperation;

tmplCallArray: name=tmplID '[' elem=tmplOperation ']';

tmplCallVariable: name=tmplID;

tmplValueType: tmplCallObj | tmplPrimitiveValue | tmplMultiValue;

tmplPrimitiveValue: tmplStringValue | tmplNumberValue | tmplTextValue | tmplEntityValue | tmplBoolValue | tmplArrayValue;

tmplStringValue: value=tmplString;

tmplNumberValue: value=NUMBER;

tmplTextValue: value=tmplText;

tmplBoolValue: value= 'true' | 'false';

tmplArrayValue: '[' (params+=tmplSetAttribute)? (',' params+=tmplSetAttribute)* ']';

tmplAttribute: ((attr=tmplID)? (':' type=tmplType)? value=tmplOperation);

tmplMultiValue: '(' (values+=tmplValueType) (',' values+=tmplValueType)* ')';

tmplEntityValue:
	'new' ('(' ((params+=tmplAttribute) (',' params+=tmplAttribute)*) ')')? '{'
	attrs+=tmplAttribute*
	'}';

tmplOperation:
     (content=tmplExpression (op=operator  next=tmplOperation)* |
     '(' content=tmplExpression ')' (op=operator  next=tmplOperation)* |
     '(' content=tmplExpression (op=operator  next=tmplOperation)* ')' |
     '(' innerBlock=tmplOperation ')' (op=operator  next=tmplOperation)*);

tmplInclude: '<[' ((calls+=callObj)*) ']>';

tmplReturn: 'return' call=tmplOperation;

tmplAffect: variable=tmplCallObj '=' value=tmplOperation;

tmplID: ID | tmplIntprID;

tmplIntprID: (pre=ID)? '${' callObj '}' (pos=ID)?;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=ID)? '${' callObj '}' (pos=ID)? '"';

tmplText: TEXT | tmplIntprText;

tmplIntprText: 's"""' (pre=ID)? '${' callObj '}' (pos=ID)? '"""';
