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
    helperIf | helperFor | helperCallFund
;

helperIf:
    'if' '(' condition=helperCondition ')' '{'
        (content+=helperStatement)*
    '}';

helperCondition:
;

helperFor:
    'for' '(' var=ID 'in' array=ID ')' '{'
        (content+=helperStatement)*
    '}'
;

helperCallFund:
    name=ID '(' ')'
;
