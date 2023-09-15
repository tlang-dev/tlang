lexer grammar CommonLexer;

TEXT:
	'"""' '"""';

WS : ( ' ' | '\t' | '\r' | '\n' )+ -> channel(HIDDEN);

fragment ESCAPED_QUOTE : '\\"';
STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

NUMBER     : '0'..'9'+ ('.' '0'..'9'+)?;

COMMENT
    : '/*' .*? '*/' -> channel(HIDDEN)
;

LINE_COMMENT
    : '//' ~[\r\n]* -> channel(HIDDEN)
;

Helper : 'helper';
Tmpl : 'tmpl';
Model: 'model';
Use: 'use';
Expose: 'expose';
Func: 'func';
If: 'if';
Else: 'else';
For: 'for';
With: 'with';
In: 'in';
To: 'to';
Until: 'until';
Set: 'set';
Let: 'let';
Impl: 'impl';
Ext: 'ext';
New: 'new';
Spec: 'spec';
Pkg: 'pkg';
While: 'while';
Do: 'do';
Var: 'var';
Return: 'return';
True: 'true';
False: 'false';
LBRACE : '{' ;
RBRACE : '}' ;
LPARENT: '(';
RPARENT: ')';
LSQUARE: '[';
RSQUARE: ']';
INTEPRETED: '${';
As: 'as';

Sync: 'sync';
Init: 'init';
Destroy: 'destroy';
Future: 'future';
Await: 'await';
Try: 'try';
Catch: 'catch';
Finally: 'finally';
Continue: 'continue';
Break: 'break';
Const: 'const';
Static: 'static';
Getter: 'getter';
Setter: 'setter';
Factory: 'factory';
Constructor: 'constructor';
Throw: 'throw';
Final: 'final';

Public: 'public';
Private: 'private';
Protected: 'protected';
Sealed: 'sealed';
Abstract: 'abstract';
Trait: 'trait';
Record: 'record';
Singleton: 'singleton';

ESCAPED_ID: '`'  ( ~('\n' | '\r' | '\t' | ' ') )*? '`';

WHITE_SPACES: [\t\u000B\u000C\u0020\u00A0]+ -> channel(HIDDEN);
//ML_COMMENT : '/*' -> '*/';
//SL_COMMENT : '//' !('\n'|'\r')* ('\r'? '\n')?;

//WS         : (' '|'\t'|'\r'|'\n')+ -> skip;
//WS     : [ \t\r\n]+ -> channel(HIDDEN);

ID:
	'^'? ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '-')*;
