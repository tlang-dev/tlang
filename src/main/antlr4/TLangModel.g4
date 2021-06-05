grammar TLangModel;

import TLangCommon, CommonLexer;

	/*
 * Model Block
 * Set the data model and the entities to personalize the generated template
 */
modelBlock:
	'model' '{'
	modelContents+=modelContent*
	'}';

//MODEL: 'model';

modelContent: assignVar | modelSetEntity;

/**
* Set entity type
*/

modelSetEntity:
    'set' name=ID ('ext' ext=objType)? ('(' ((params+=modelSetAttribute) (',' params+=modelSetAttribute)*) ')')? '{'
    (attrs+=modelSetAttribute (',' attrs+=modelSetAttribute)*)?
    '}'
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

modelSetImpl: 'impl' ('{' attrs+=modelSetAttribute (',' attrs+=modelSetAttribute)* '}')?;

modelSetImplArray: 'impl' '[' ']';
