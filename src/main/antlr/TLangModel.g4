grammar TLangModel;

	/*
 * Model Block
 * Set the data model and the entities to personalize the generated template
 */
modelBlock:
	'model' '{'
	modelEntities+=modelContent*
	'}';

MODEL: 'model';

modelContent: modelNewEntity | modelSetEntity;

/**
* Create new Entity
*/
modelNewEntity:
	type=ID ('(' ((attrs+=modelAttribut) (',' attrs+=modelAttribut)*) ')')? '{'
	decl+=modelValueType*
	'}';

modelValueType:
	(modelAttribut | modelEntityAsAttribut | modelTbl);

modelTbl:
	attr=ID? ('[')
	((elms+=modelValueType) (',' elms+=modelValueType)*)
	']';

modelEntityAsAttribut:
	(attr=ID? value=modelNewEntity);

modelAttribut:
	(attr=ID? value=STRING);

/**
* Set entity type
*/

modelSetEntity:
    'set' name=ID ('(' ((attrs+=modelSetAttribut) (',' attrs+=modelSetAttribut)*) ')')? '{'
    '}'
;

modelSetAttribut:
    attr=ID? value=modelSetValueType;

modelSetValueType:
    modelSetType | modelSetFuncDef | modelSetRef
;

modelSetType:
    type=ID ('<' (generic=tmplGeneric) '>')? (array='[' ']')?;

    tmplGeneric:
        (types+=modelSetType (',' types+=modelSetType)*);

modelSetFuncDef: '('  ')';

modelSetRef: '->' ref=ID;
