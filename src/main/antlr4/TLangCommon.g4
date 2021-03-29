grammar TLangCommon;

import CommonLexer;

/* Common elements */

assignVar: 'let' name=ID (':' type=ID)? '=' value=operation;

operation: (content=complexValueType (op=operator  next=operation)* |
               '(' content=complexValueType ')' (op=operator  next=operation)* |
               '(' content=complexValueType (op=operator  next=operation)* ')' |
               '(' innerBlock=operation ')' (op=operator  next=operation)*);

operator: '&&' | '||' | '+' | '-' | '*' | '/' | '%' | '==' | '!=' | '>' | '<' | '>=' | '<=' ;

complexValueType: callObj | primitiveValue | multiValue | lazyValue;

//simpleValueType: callObj | primitiveValue;

primitiveValue: stringValue | numberValue | textValue | entityValue | boolValue | arrayValue;

lazyValue: '_';

stringValue: value=STRING;

numberValue: value=NUMBER;

textValue: value=TEXT;

boolValue: value= 'true' | 'false';

arrayValue: '[' (params+=complexAttribute)? (',' params+=complexAttribute)* ']';

callObj: objs+=callObjType ('.' objs+=callObjType)*;

callObjType: callArray | (ref='&')? callFunc | callVariable;

callArray: name=ID '[' elem=operation ']';

callFunc: ((name=ID) | '_') (currying += curryParams)+;

curryParams:'(' (params+=setAttribute (',' params+=setAttribute)*)? ')';

setAttribute: (attr=ID '=')? value=operation;

callVariable: name=ID;

entityValue:
	 ('(' ((attrs+=complexAttribute) (',' attrs+=complexAttribute)*) ')')? '{'
	decl+=complexAttribute*
	'}';

multiValue: '(' (values+=operation) (',' values+=operation)+ ')'; // '+' is needed to avoid confusion with operation

complexAttribute: (((attr=ID) (':' type=ID)? '=')? value=operation);
