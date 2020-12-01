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
* Create new Entity
*/
//modelNewEntity:
//    'let' name=ID entity=modelNewEntityValue
//;
//
//modelNewEntityValue:
//	(type=ID)? ('(' ((attrs+=modelValueType) (',' attrs+=modelValueType)*) ')')? '{'
//	decl+=modelValueType*
//	'}';
//
//modelValueType:
//	(modelAttribute | modelEntityAsAttribute | modelArray);
//
//modelArray:
//	attr=ID? ('[')
//	((elms+=modelValueType) (',' elms+=modelValueType)*)
//	']';
//
//modelEntityAsAttribute:
//	(attr=ID? value=modelNewEntityValue);
//
//modelAttribute:
//	(attr=ID? value=STRING);

/**
* Set entity type
*/

modelSetEntity:
    'set' name=ID ('(' ((params+=modelSetAttribute) (',' params+=modelSetAttribute)*) ')')? '{'
    attrs+=modelSetAttribute*
    '}'
;

//SET: 'set';

modelSetAttribute:
    attr=ID? value=modelSetValueType;

modelSetValueType: modelSetType | modelSetArray | modelSetFuncDef | modelSetRef;

modelSetType: type=ID;

modelSetArray: array=ID '[' ']';

modelSetFuncDef: '(' (paramTypes+=modelSetValueType (',' paramTypes+=modelSetValueType)*)? ')' (':'  '(' retTypes+=modelSetValueType (',' retTypes+=modelSetValueType)* ')' )?;

modelSetRef: '&' refs+=ID ('.' refs+=ID)* (('(' currying+=modelSetRefCurrying  ')') ('(' currying+=modelSetRefCurrying ')')*)?;

modelSetRefCurrying:values+=modelSetRefValue (',' values+=modelSetRefValue)*;

modelSetRefValue: modelSetRef | complexValueType;
