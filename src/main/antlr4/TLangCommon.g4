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

//conditionBlock:
//     content=condition (link=('&&' | '||')  next=conditionBlock)* |
//    '(' content=condition ')' (link=('&&' | '||')  next=conditionBlock)* |
//    '(' content=condition (link=('&&' | '||')  next=conditionBlock)* ')' |
//    '(' innerBlock=conditionBlock ')' (link=('&&' | '||')  next=conditionBlock)*
//;
//
//condition:
//    arg1=simpleValueType (mark=conditionMark arg2=simpleValueType)? (link=('&&' | '||') next=conditionBlock)*
//;
//
//conditionMark: '==' | '!=' | '<' | '>' | '<=' | '>=';

entityValue:
	 ('(' ((attrs+=complexAttribute) (',' attrs+=complexAttribute)*) ')')? '{'
	decl+=complexAttribute*
	'}';

multiValue: '(' (values+=operation) (',' values+=operation)* ')';

complexAttribute: (((attr=ID) (':' type=ID)? '=')? value=operation);

//simpleAttribute: (((attr=ID) (':' type=ID)? '=')? value=operation);
