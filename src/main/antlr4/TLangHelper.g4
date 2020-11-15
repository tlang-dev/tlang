grammar TLangHelper;

import CommonLexer;

/*
 * Helper block
 * The helper is interpreted and therefore, offers dynamic results for the template
 */
helperBlock:
	'helper' '{'
	(helperFuncs+=helperFunc)*
	'}';

helperFunc:
	'func' name=ID ('(' (currying+=helperCurrying)? ')')* (':' retVals+=helperParamType (',' retVals+=helperParamType)*)?'{'
	    body=helperContent
	'}';

helperCurrying: params += helperParam (',' params += helperParam)*;

helperParam: (param=ID)? type=helperParamType;

helperParamType: helperObjType | helperArrayType | helperFuncType;

helperObjType: tpye=ID;

helperArrayType: tpye=ID'['']';

helperFuncType: type='(' (currying+=helperCurrying)? ')' ('(' currying+=helperCurrying ')')* ':' retVals+=helperParamType (',' retVals+=helperParamType)*;

helperContent: content+=helperStatement*;

helperStatement: helperIf | helperFor | helperCallObj | helperConditionBlock;

helperIf:
    'if' '(' condition=helperConditionBlock ')' '{'
        body=helperContent
    '}' orElse=helperElse?;

helperElse:
    'else' '{'
        body=helperContent
    '}';

helperConditionBlock: '('? content+=helperCondition ')'? (link=('&&' | '||') helperConditionBlock)*;

helperCondition:
    arg1=helperCallObj (mark=conditionMark arg2=helperCallObj)? (link=('&&' | '||') helperCondition)*
;

conditionMark: '==' | '!=' | '<' | '>' | '<=' '>=';

helperFor:
    'for' '(' var=ID type=('in' | 'to' | 'until') array=helperCallObj ')' '{'
        body=helperContent
    '}'
;

helperCallObj: objs+=helperCallObjType ('.'objs+=helperCallObjType)*;

helperCallObjType:
    helperCallArray | helperCallString | helperCallText | helperCallNumber | helperCallFunc | helperCallVariable
;

helperCallString: type=STRING;

helperCallNumber: type=NUMBER;

helperCallText: type=TEXT;

helperCallArray: (name=ID)? '[' elem=helperCallObj ']';

helperCallFunc:
    ((name=ID) | '_') currying += '(' params+=helperCallObj (',' params+=helperCallObj)* ')' (currying+='(' params+=helperCallObj (',' params+=helperCallObj)* ')')*
;

helperCallVariable: name=ID;
