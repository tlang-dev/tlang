grammar TLangCommon;

import CommonLexer;

/* Common elements */

assignVar: 'let' name=ID (':' type=ID)? '=' value=complexValueType;

complexValueType: callObj | primitiveValue | conditionBlock | multiValue;

simpleValueType: callObj | primitiveValue;

primitiveValue: stringValue | numberValue | textValue | entityValue | boolValue | arrayValue;

stringValue: value=STRING;

numberValue: value=NUMBER;

textValue: value=TEXT;

boolValue: value= 'true' | 'false';

arrayValue: '[' (params+=simpleAttribute)? (',' params+=simpleAttribute)* ']';

callObj: objs+=callObjType ('.'objs+=callObjType)*;

callObjType: callArray | callFunc | callVariable;

callArray: name=ID '[' elem=simpleValueType ']';

callFunc:
    ((name=ID) | '_') (currying += curryParams)+
;

curryParams:'(' (params+=setAttribute (',' params+=setAttribute)*)? ')';

setAttribute: (attr=ID '=')? value=complexValueType;

callVariable: name=ID;

conditionBlock:
     content=condition (link=('&&' | '||')  next=conditionBlock)* |
    '(' content=condition ')' (link=('&&' | '||')  next=conditionBlock)* |
    '(' content=condition (link=('&&' | '||')  next=conditionBlock)* ')' |
    '(' innerBlock=conditionBlock ')' (link=('&&' | '||')  next=conditionBlock)*
;

condition:
    arg1=simpleValueType (mark=conditionMark arg2=simpleValueType)? (link=('&&' | '||') next=conditionBlock)*
;

conditionMark: '==' | '!=' | '<' | '>' | '<=' | '>=';

entityValue:
	 ('(' ((attrs+=complexAttribute) (',' attrs+=complexAttribute)*) ')')? '{'
	decl+=complexAttribute*
	'}';

multiValue: '(' (values+=complexValueType) (',' values+=complexValueType)* ')';

complexAttribute: (((attr=ID) (':' type=ID)? '=')? value=complexValueType);

simpleAttribute: (((attr=ID) (':' type=ID)? '=')? value=simpleValueType);
