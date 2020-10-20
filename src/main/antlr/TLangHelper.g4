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
	'func' name=ID '{'
	    body=helperContent
	'}';

helperContent: content+=helperStatement*;

helperStatement:
    helperIf | helperFor | helperCallObj
;

helperIf:
    'if' '(' condition=helperCondition ')' '{'
        body=helperContent
    '}' else=helperElse?;

helperElse:
    'else' '{'
        body=helperContent
    '}';

helperCondition:
    arg1=helperCallObj (mark=conditionMark arg2=helperCallObj)?
;

conditionMark: '==' | '!=' | '<' | '>' | '<=' '>=';

helperFor:
    'for' '(' var=ID 'in' array=ID ')' '{'
        body=helperContent
    '}'
;

helperCallObj:
    helperCallArray | helperCallFunc | helperCallVariable
;

helperCallArray:
    name=ID '[' elem=ID ']'
;

helperCallFunc:
    name=ID '(' ')'
;

helperCallVariable:
    name=ID
;
