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

COLON : ':';
EQUALS : '=';
PERIOD: '.';
UNDERSCORE: '_';
COMMA: ',';
PLUS: '+';
MINUS: '-';
MULTIPLY: '*';
AND: '&';
ANDAND: '&&';
OROR: '||';
SLASH: '/';
PERCENT: '%';
EQUALQUAL: '==';
NOTEQUAL: '!=';
LESS: '<';
IS_MORE: '>';
LESSEQUAL: '<=';
MOREEQUAL: '>=';
EQUAL_MORE: '=>';
AT: '@';
COLON_EQUAL: ':=';
INTERROGATION: '?';
INTERROGATION_INTERROGATION: '??';
INTERROGATION_DOT: '?.';
EXCLAMATION: '!';
EXCLAMATION_DOT: '!.';
EXCLA_EXCLA_DOT: '!!.';
START_INCLUDE: '<[';
END_INCLUDE: ']>';
QUOTE: '"';
TEXT_QUOTE: '"""';
INTER_TEXT_QUOTE: 's"""';
INTER_QUOTE: 's"';
COLON_COLON: '::';
SET_ATTR: 'setAttr';

DOC: 'doc';
LANG: 'lang';
DATA: 'data';
CMD: 'cmd';
STYLE: 'style';

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



//START_DOC: 'START_DOC';
//END_DOC: 'END_DOC';
START_DOC: '--->' -> pushMode(DOC_MODE);

mode DOC_MODE;


//DOC_SPACES : ( ' ' | '\t' | '\r' | '\n' )+;
WHITESPACE : [ \t\r\n]+ -> channel(HIDDEN);

DOC_STRING :   '"' ( ESCAPED_QUOTE | ~('\n'|'\r') )*? '"';

fragment PLAIN_TEXT: ~('{' | '}' | '[' | ']' | ',' | '*' | '|' | '#' | '(' | ')' | '=' | ' ' | '\t' | '\n' | '\r')*;
//PLAIN_TEXT: .+?;

DOC_RSQUARE: '/]';

IMG: '[img';
TABLE: '[table';
LINK: '[link';
CODE: '[code';
LIST: '[list';
INCLUDE: '[include';
SPAN: '[span';
TYPE: 'type';
BULLET_LIST: 'bullet';
NUMBER_LIST: 'number';
LEVEL1: '#';
LEVEL2: '##';
LEVEL3: '###';
PIPE: '|';

END_DOC: '<---' -> popMode;

