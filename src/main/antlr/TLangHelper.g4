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
	    (content+=helperStatement)*
	'}';

helperStatement:
    helperIf | helperFor | helperCallObj
;

helperIf:
    'if' '(' condition=helperCondition ')' '{'
        (content+=helperStatement)*
    '}';

helperCondition:
    arg1=helperCallObj (mark=conditionMark arg2=helperCallObj)?
;

conditionMark: '==' | '!=' | '<' | '>' | '<=' '>=';

helperFor:
    'for' '(' var=ID 'in' array=ID ')' '{'
        (content+=helperStatement)*
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
