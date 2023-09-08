grammar TLangHelper;

import TLangCommon, CommonLexer;

/*
 * Helper block
 * The helper is interpreted and therefore, offers dynamic results for the template
 */
helperBlock:
	Helper LBRACE
	(helperFuncs+=helperFunc)*
	RBRACE;

helperFunc:
	Func name=ID ('(' (currying+=helperCurrying)? ')')* (':' retVals+=helperParamType (',' retVals+=helperParamType)*)?LBRACE
	    body=helperContent
	RBRACE;

helperCurrying: params += helperParam (',' params += helperParam)*;

helperParam: (param=ID ':')? type=helperParamType;

helperParamType: objType | arrayType | helperFuncType;

helperFuncType: type='(' (currying+=helperCurrying)? ')' ('(' currying+=helperCurrying ')')* '=>' retVals+=helperParamType (',' retVals+=helperParamType)*;

helperContent: content+=helperStatement*;

helperStatement: assignVar | operation | helperIf | helperFor;

helperIf:
    If '(' cond=operation ')' LBRACE
        body=helperContent
    RBRACE orElse=helperElse?;

helperElse:
    Else LBRACE
        body=helperContent
    RBRACE;

helperFor:
   For '(' var=ID start=operation? type=(In | To | Until) array=operation ')' LBRACE
        body=helperContent
    RBRACE
;
