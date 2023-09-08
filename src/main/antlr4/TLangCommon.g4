grammar TLangCommon;

import CommonLexer;

/* Common elements */

assignVar: Let name=ID (':' type=valueType)? '=' value=operation;

valueType: objType | arrayType;

objType: (exTpye=ID '.')? type=ID;

arrayType: (exTpye=ID '.')? type=ID '['']';

operation: (content=complexValueType (op=operator  next=operation)* |
               '(' content=complexValueType ')' (op=operator  next=operation)* |
               '(' content=complexValueType (op=operator  next=operation)* ')' |
               '(' innerBlock=operation ')' (op=operator  next=operation)*);

operator: '&&' | '||' | '+' | '-' | '*' | '/' | '%' | '==' | '!=' | '>' | '<' | '>=' | '<=' ;

complexValueType: callObj | primitiveValue | multiValue | lazyValue | impl;

primitiveValue: stringValue | numberValue | textValue | entityValue | boolValue | arrayValue;

lazyValue: '_';

stringValue: value=STRING;

numberValue: value=NUMBER;

textValue: value=TEXT;

boolValue: True | False;

arrayValue: '[' (params+=complexAttribute)? (',' params+=complexAttribute)* ']';

callObj: objs+=callObjType ('.' objs+=callObjType)*;

callObjType: callArray | (ref='&')? callFunc | callVariable;

callArray: name=ID '[' elem=operation ']';

callFunc: ((name=ID) | '_') (currying += curryParams)+;

curryParams:'(' (params+=setAttribute (',' params+=setAttribute)*)? ')';

setAttribute: (attr=ID '=')? value=operation;

callVariable: name=ID;

entityValue: LBRACE attrs+=complexAttribute* RBRACE;

impl: Impl (':' type=ID)? LBRACE (attrs+=complexAttribute)? (',' attrs+=complexAttribute)* '}';

multiValue: '(' (values+=operation) (',' values+=operation)+ ')'; // '+' is needed to avoid confusion with operation

complexAttribute: (((attr=ID) (':' type=valueType)? '=')? value=operation);
