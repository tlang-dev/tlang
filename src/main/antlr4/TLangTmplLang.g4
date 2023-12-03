parser grammar TLangTmplLang;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}

tmplLang: 'lang' content = tmplFullBlock | tmplSpecialisedBlock;

tmplFullBlock: LBRACE
	(tmplPakage=tmplPkg)?
	(tmplUses+=tmplUse)*
	(tmplContents+=tmplContent)*
	RBRACE;

tmplSpecialisedBlock: Spec LBRACE content=tmplSpecialisedContent RBRACE;

//tmplSpecialisedTypes: 'impl' | 'func' | 'expr' | 'attr' | 'setAttr' | 'param';

tmplSpecialisedContent: tmplContent | tmplAttribute | 'setAttr' tmplSetAttribute | tmplParam;

tmplContent: tmplImpl | tmplFunc | tmplSpecialBlock | tmplExpression | tmplOperation;

tmplPkg: Pkg parts+=tmplID ('.' parts+=tmplID)*;

tmplUse: Use parts+=tmplID ('.' parts+=tmplID)* (As alias=tmplID)?;

tmplAnnot: '@' name=tmplID (LPARENT annotParams+=tmplAnnotParam (',' annotParams+=tmplAnnotParam)* RPARENT)?;

tmplAnnotParam:(name=tmplID '=')? value=tmplValueType;

tmplProps: ('[' (props+=tmplID)+ RSQUARE)?;

tmplImpl:
    (annots+=tmplAnnot)*
	Impl props=tmplProps  name=tmplID ((For forProps=tmplProps  forNames+=tmplType) (',' forNames+=tmplType)* ((With withProps=tmplProps  withNames+=tmplType) (',' withNames+=tmplType)*)?)? LBRACE
	(tmplImplContents+=tmplContent)*
	RBRACE;

tmplFunc:
    (annots+=tmplAnnot)*
	Func props=tmplProps ((preNames+=tmplID)* '.')? name=tmplID curries+=tmplCurrying* (':' types+=tmplType (',' types+=tmplType)*)? postProps=tmplProps content=tmplExprContent?;

tmplCurrying: LPARENT ((params+=tmplCurryingParamType) (',' params+=tmplCurryingParamType)*) RPARENT;

tmplCurryingParamType: tmplCurryingParam | tmplMandatoryParams | tmplPositionParams;

tmplMandatoryParams: LBRACE param=tmplCurryingParam RBRACE;

tmplPositionParams: LSQUARE param=tmplCurryingParam RSQUARE;

tmplCurryingParam:
	((params+=tmplParam) (',' params+=tmplParam)*)?;

tmplParam:
    (annots+=tmplAnnot)*
	accessor=tmplID? name=tmplID (':' type=tmplType)?;

tmplType:
	type=tmplID ('<' (generic=tmplGeneric) '>')? (currying += tmplCurryParams)* (array='[' RSQUARE)?;

tmplGeneric:
	(types+=tmplType (',' types+=tmplType)*);

tmplExprContent: tmplExpression | tmplExprBlock;

tmplExprBlock: LBRACE exprs+=tmplExpression* RBRACE;

tmplExpression:	tmplVar | tmplCallObj | tmplValueType | tmplFunc
                | tmplIf | tmplFor | tmplWhile | tmplDoWhile | tmplInclude | tmplReturn
                | tmplAffect | tmplCast | tmplAnonFunc | tmplPrimitiveValue | tmplSpecialBlock;

tmplIf: If LPARENT cond=tmplOperation RPARENT content=tmplExprContent elseThen=tmplElse?;

tmplElse: Else (tmplIf | tmplExprContent);

tmplFor: For LPARENT variable=tmplID ('=' start=tmplOperation)? type=('in' | 'to' | 'until') array=tmplOperation RPARENT content=tmplExprContent;

tmplWhile: While LPARENT cond=tmplOperation RPARENT content=tmplExprContent;

tmplDoWhile: Do content=tmplExprContent While LPARENT cond=tmplOperation RPARENT;

tmplAnonFunc: params=tmplCurrying '=>' content=tmplExprContent;

tmplVar:
    (annots+=tmplAnnot)*
    Var props=tmplProps name=tmplID (':' type=tmplType)? (optional=tmplOptionalValue)? (':=' value=tmplOperation)?;

tmplOptionalValue: '?';

tmplCallObj: props=tmplProps firstCall=tmplCallObjType objs+=tmplCallObjLink*;

tmplCallObjLink: ((access= ('.' | '?.' | '!.' | '!!.' | '::' | '??')) obj=tmplCallObjType) (postAccess='!')?;

tmplCallObjType: tmplCallArray | tmplCallFunc | tmplCallVariable | tmplPrimitiveValue;

tmplCallFunc: ((name=tmplID) | '_') (currying += tmplCurryParams)+;

tmplCurryParams:LPARENT (params+=tmplInclSetAttribute (',' params+=tmplInclSetAttribute)*)? RPARENT;

tmplSetAttribute: (name=tmplIdOrString ':')? value=tmplOperation;

tmplInclSetAttribute: tmplInclude | tmplSetAttribute;

tmplCallArray: name=tmplID '[' elem=tmplOperation RSQUARE;

tmplCallVariable: name=tmplID;

tmplValueType: tmplCallObj | tmplPrimitiveValue | tmplMultiValue;

tmplPrimitiveValue: tmplStringValue | tmplNumberValue | tmplTextValue | tmplEntityValue | tmplBoolValue | tmplArrayValue;

tmplStringValue: value=tmplString;

tmplNumberValue: value=NUMBER;

tmplTextValue: value=tmplText;

tmplBoolValue: value= True | False;

tmplArrayValue: '[' (params+=tmplInclSetAttribute)? (',' params+=tmplInclSetAttribute)* RSQUARE;

tmplInclAttribute: tmplInclude | tmplAttribute;

tmplAttribute: ((attr=tmplID)? (':' type=tmplType)? value=tmplOperation);

tmplMultiValue: LPARENT (values+=tmplValueType) (',' values+=tmplValueType)* RPARENT;

tmplEntityValue:
	New (name=tmplID)? (LPARENT ((params+=tmplInclSetAttribute) (',' params+=tmplInclSetAttribute)*)? RPARENT)?
	(LBRACE ((attrs+=tmplInclSetAttribute) (',' attrs+=tmplInclSetAttribute)*)? RBRACE)?;

tmplOperation:
     (content=tmplExpression (op=operator  next=tmplOperation)* |
     LPARENT content=tmplExpression RPARENT (op=operator  next=tmplOperation)* |
     LPARENT content=tmplExpression (op=operator  next=tmplOperation)* RPARENT |
     LPARENT innerBlock=tmplOperation RPARENT (op=operator  next=tmplOperation)*)
     ('.' combine=tmplCallObj)?;

tmplInclude: '<[' ((calls+=callObj)*) ']>';

tmplReturn: Return call=tmplOperation;

tmplAffect: variable=tmplCallObj '=' value=tmplOperation;

tmplCast: LPARENT toCast=tmplOperation 'as' type=tmplType RPARENT ('.' combine=tmplCallObj)?;

tmplID: ID | tmplIntprID | ESCAPED_ID;

tmplIntprID: (pre=ID)? INTEPRETED callObj RBRACE (pos=ID)?;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? '"';

tmplText: TEXT | tmplIntprText;

tmplIntprText: 's"""' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? '"""';

tmplIdOrString: tmplID | tmplString;

tmplSpecialBlock: type=( Sync | Init | Destroy |Future|Await|Try|Catch|Finally|Continue|Break|Const|Static|Getter|Setter|Factory|Constructor|Throw| Final ) curries+=tmplCurrying* (expr=tmplExprContent)?;