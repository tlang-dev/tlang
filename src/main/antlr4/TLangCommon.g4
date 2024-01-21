parser grammar TLangCommon;

options {
  tokenVocab = CommonLexer;
}


/* Common elements */

assignVar: Let name=ID (':' type=valueType)? '=' value=operation;

valueType: objType | arrayType;

objType: (exTpye=ID '.')? type=ID;

arrayType: (exTpye=ID '.')? type=ID '['RSQUARE;

operation: (content=complexValueType (op=operator  next=operation)* |
               '(' content=complexValueType ')' (op=operator  next=operation)* |
               '(' content=complexValueType (op=operator  next=operation)* ')' |
               '(' innerBlock=operation ')' (op=operator  next=operation)*);

operator: '&&' | '||' | '+' | '-' | MULTIPLY | '/' | '%' | '==' | '!=' | '>' | '<' | '>=' | '<=' ;

complexValueType: callObj | primitiveValue | multiValue | lazyValue | impl | either;

primitiveValue: stringValue | numberValue | textValue | entityValue | boolValue | arrayValue;

lazyValue: '_';

either: left=valueType '|' right=valueType;

stringValue: value=STRING;

numberValue: value=NUMBER;

textValue: value=TEXT;

boolValue: True | False;

arrayValue: '[' (params+=complexAttribute)? (',' params+=complexAttribute)* RSQUARE;

callObj: objs+=callObjType ((callType='.' | '?.') objs+=callObjType)* ('??' nonNull=callObjType)?;

callObjType: callArray | (ref='&')? callFunc | callVariable;

callArray: name=ID '[' elem=operation RSQUARE;

callFunc: ((name=ID) | '_') (currying += curryParams)+
    (Then content=operation)?
    (Catch content=operation)?
    (Finally content=operation)?;

curryParams:'(' (params+=setAttribute (',' params+=setAttribute)*)? ')';

setAttribute: (attr=ID '=')? value=operation;

callVariable: name=ID;

entityValue: LBRACE attrs+=complexAttribute* RBRACE;

impl: Impl (':' type=ID)? LBRACE (attrs+=complexAttribute)? (',' attrs+=complexAttribute)* RBRACE;

multiValue: '(' (values+=operation) (',' values+=operation)+ ')'; // '+' is needed to avoid confusion with operation

complexAttribute: (((attr=ID) (':' type=valueType)? '=')? value=operation);
