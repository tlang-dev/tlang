grammar TLangTmpl;

import TLangCommon, TLangHelper, CommonLexer;

	/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock:
	'tmpl' '[' lang=tmplID ']' name=tmplID ('('params += helperParam (',' params += helperParam)*')')? block = tmplBlockContentType;

tmplBlockContentType: tmplFullBlock | tmplSpecialisedBlock;

tmplFullBlock: '{'
	(tmplPakage=tmplPkg)?
	(tmplUses+=tmplUse)*
	(tmplContents+=tmplContent)*
	'}';

tmplSpecialisedBlock: 'spec' '{' content=tmplSpecialisedContent '}';

//tmplSpecialisedTypes: 'impl' | 'func' | 'expr' | 'attr' | 'setAttr' | 'param';

tmplSpecialisedContent: tmplContent | tmplAttribute | 'setAttr' tmplSetAttribute | tmplParam;

tmplContent: tmplImpl | tmplFunc | tmplExpression | tmplOperation;

tmplPkg: 'pkg' parts+=tmplID ('.' parts+=tmplID)*;

tmplUse: 'use' parts+=tmplID ('.' parts+=tmplID)* ('as' alias=tmplID)?;

tmplAnnot: '@' name=tmplID ('(' annotParams+=tmplAnnotParam (',' annotParams+=tmplAnnotParam)* ')')?;

tmplAnnotParam:name=tmplID '=' value=tmplValueType;

tmplProps: ('[' (props+=tmplID)+ ']')?;

tmplImpl:
    (annots+=tmplAnnot)*
	'impl' props=tmplProps  name=tmplID (('for' forProps=tmplProps  forNames+=tmplType) (',' forNames+=tmplType)* (('with' withProps=tmplProps  withNames+=tmplType) (',' withNames+=tmplType)*)?)? '{'
	(tmplImplContents+=tmplContent)*
	'}';

tmplFunc:
    (annots+=tmplAnnot)*
	'func' props=tmplProps name=tmplID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? postProps=tmplProps content=tmplExprBlock?;

tmplCurrying: '(' param=tmplCurryingParam ')';

tmplCurryingParam:
	((params+=tmplParam) (',' params+=tmplParam)*)?;

tmplParam:
    (annots+=tmplAnnot)*
	accessor=tmplID? name=tmplID (':' type=tmplType)?;

tmplType:
	type=tmplID ('<' (generic=tmplGeneric) '>')? (instance=tmplCurryParams)? (array='[' ']')?;

tmplGeneric:
	(types+=tmplType (',' types+=tmplType)*);

tmplExprContent: tmplExpression | tmplExprBlock;

tmplExprBlock: '{' exprs+=tmplExpression* '}';

tmplExpression:	tmplVar | tmplCallObj | tmplValueType | tmplFunc
                | tmplIf | tmplFor | tmplWhile | tmplDoWhile | tmplInclude | tmplReturn
                | tmplAffect | tmplCast | tmplAnonFunc | tmplPrimitiveValue;

tmplIf: 'if' '(' cond=tmplOperation ')' content=tmplExprContent elseThen=tmplElse?;

tmplElse: 'else' (tmplIf | tmplExprContent);

tmplFor: 'for' '(' variable=tmplID ('=' start=tmplOperation)? type=('in' | 'to' | 'until') array=tmplOperation ')' content=tmplExprContent;

tmplWhile: 'while' '(' cond=tmplOperation ')' content=tmplExprContent;

tmplDoWhile: 'do' content=tmplExprContent 'while' '(' cond=tmplOperation ')';

tmplAnonFunc: params=tmplCurrying '=>' content=tmplExprContent;

tmplVar:
    (annots+=tmplAnnot)*
    'var' props=tmplProps name=tmplID (':' type=tmplType)? (optional=tmplOptionalValue)? ('=' value=tmplOperation)?;

tmplOptionalValue: '?';

tmplCallObj: props=tmplProps objs+=tmplCallObjType ('.'objs+=tmplCallObjType)*;

tmplCallObjType: tmplCallArray | tmplCallFunc | tmplCallVariable;

tmplCallFunc: ((name=tmplID) | '_') (currying += tmplCurryParams)+;

tmplCurryParams:'(' (params+=tmplInclSetAttribute (',' params+=tmplInclSetAttribute)*)? ')';

tmplSetAttribute: (name=tmplIdOrString ':')? value=tmplOperation;

tmplInclSetAttribute: tmplInclude | tmplSetAttribute;

tmplCallArray: name=tmplID '[' elem=tmplOperation ']';

tmplCallVariable: name=tmplID;

tmplValueType: tmplCallObj | tmplPrimitiveValue | tmplMultiValue;

tmplPrimitiveValue: tmplStringValue | tmplNumberValue | tmplTextValue | tmplEntityValue | tmplBoolValue | tmplArrayValue;

tmplStringValue: value=tmplString;

tmplNumberValue: value=NUMBER;

tmplTextValue: value=tmplText;

tmplBoolValue: value= 'true' | 'false';

tmplArrayValue: '[' (params+=tmplInclSetAttribute)? (',' params+=tmplInclSetAttribute)* ']';

tmplInclAttribute: tmplInclude | tmplAttribute;

tmplAttribute: ((attr=tmplID)? (':' type=tmplType)? value=tmplOperation);

tmplMultiValue: '(' (values+=tmplValueType) (',' values+=tmplValueType)* ')';

tmplEntityValue:
	'new' (name=tmplID)? ('(' ((params+=tmplInclAttribute) (',' params+=tmplInclAttribute)*)? ')')?
	('{' ((attrs+=tmplInclAttribute) (',' attrs+=tmplInclAttribute)*)? '}')?;

tmplOperation:
     (content=tmplExpression (op=operator  next=tmplOperation)* |
     '(' content=tmplExpression ')' (op=operator  next=tmplOperation)* |
     '(' content=tmplExpression (op=operator  next=tmplOperation)* ')' |
     '(' innerBlock=tmplOperation ')' (op=operator  next=tmplOperation)*)
     ('.' combine=tmplCallObj)?;

tmplInclude: '<[' ((calls+=callObj)*) ']>';

tmplReturn: 'return' call=tmplOperation;

tmplAffect: variable=tmplCallObj '=' value=tmplOperation;

tmplCast: '(' toCast=tmplOperation 'as' type=tmplType ')' ('.' combine=tmplCallObj)?;

tmplID: ID | tmplIntprID | ESCAPED_ID;

tmplIntprID: (pre=ID)? '${' callObj '}' (pos=ID)?;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=.)? '${' callObj '}' (pos=.)? '"';

tmplText: TEXT | tmplIntprText;

tmplIntprText: 's"""' (pre=.)? '${' callObj '}' (pos=.)? '"""';

tmplIdOrString: tmplID | tmplString;

//ANY_STRING: ('\\' .)+;
//ANY_STRING: ;