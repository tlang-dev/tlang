grammar TLangHelper;

import TLangCommon, CommonLexer;

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

helperParam: (param=ID ':')? type=helperParamType;

helperParamType: objType | arrayType | helperFuncType;

helperFuncType: type='(' (currying+=helperCurrying)? ')' ('(' currying+=helperCurrying ')')* '=>' retVals+=helperParamType (',' retVals+=helperParamType)*;

helperContent: content+=helperStatement*;

helperStatement: assignVar | operation | helperIf | helperFor;

helperIf:
    'if' '(' cond=operation ')' '{'
        body=helperContent
    '}' orElse=helperElse?;

helperElse:
    'else' '{'
        body=helperContent
    '}';

helperFor:
    'for' '(' var=ID start=operation? type=('in' | 'to' | 'until') array=operation ')' '{'
        body=helperContent
    '}'
;
