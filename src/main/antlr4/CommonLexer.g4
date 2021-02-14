lexer grammar CommonLexer;

TEXT:
	'"""' '"""';

ID:
	'^'? ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')*;

WS : ( ' ' | '\t' | '\r' | '\n' )+ -> channel(HIDDEN);

fragment ESCAPED_QUOTE : '\\"';
STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

NUMBER     : '0'..'9'+ ('.' '0'..'9'+)?;

COMMENT
    : '/*' .*? '*/' -> skip
;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip
;



//ML_COMMENT : '/*' -> '*/';
//SL_COMMENT : '//' !('\n'|'\r')* ('\r'? '\n')?;

//WS         : (' '|'\t'|'\r'|'\n')+ -> skip;
//WS     : [ \t\r\n]+ -> channel(HIDDEN);
