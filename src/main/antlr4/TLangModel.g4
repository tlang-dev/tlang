grammar TLangModel;

import TLangCommon, CommonLexer;

	/*
 * Model Block
 * Set the data model and the entities to personalize the generated template
 */
modelBlock:
	Model LBRACE
	modelContents+=modelContent*
	RBRACE;

//MODEL: 'model';

modelContent: assignVar | modelSetEntity;

/**
* Set entity type
*/

modelSetEntity:
    Set name=ID (Ext ext=objType)? ('(' ((params+=modelSetAttribute) (',' params+=modelSetAttribute)*) ')')? LBRACE
    (attrs+=modelSetAttribute (',' attrs+=modelSetAttribute)*)?
    RBRACE
;

//SET: 'set';

modelSetAttribute:
    (attr=ID ':')? value=modelSetValueType;

modelSetValueType: modelSetType | modelSetArray | modelSetFuncDef | modelSetRef | modelSetImpl | modelSetImplArray;

modelSetType: type=ID;

modelSetArray: array=ID '[' ']';

modelSetFuncDef: '(' (paramTypes+=modelSetValueType (',' paramTypes+=modelSetValueType)*)? ')' (':'  '(' retTypes+=modelSetValueType (',' retTypes+=modelSetValueType)* ')' )?;

modelSetRef: '&' refs+=ID ('.' refs+=ID)* ('(' (currying+=modelSetRefCurrying)?  ')' ('(' (currying+=modelSetRefCurrying)? ')')*)?;

modelSetRefCurrying:values+=modelSetRefValue (',' values+=modelSetRefValue)*;

modelSetRefValue: modelSetRef | operation;

modelSetImpl: Impl (LBRACE attrs+=modelSetAttribute (',' attrs+=modelSetAttribute)* RBRACE)?;

modelSetImplArray: Impl '[' ']';
