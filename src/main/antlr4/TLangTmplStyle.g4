parser grammar TLangTmplStyle;

import TLangTmpl;

options {
  tokenVocab = CommonLexer;
}

tmplStyle: STYLE LSQUARE (langs+=ID (',' langs+=ID)*) RSQUARE name=ID LPARENT (params += helperParam (',' params += helperParam)*)?RPARENT  LBRACE content=styleStruct RBRACE;

styleStruct: (name=tmplID)? (LSQUARE ((params+=styleAttribute) (',' params+=styleAttribute)*)? RSQUARE)?
              	(LBRACE ((attrs+=styleAttribute) (',' attrs+=styleAttribute)*)? RBRACE);

styleAttribute: styleInclude | styleSetAttribute;

styleInclude: START_INCLUDE (call=callObj) END_INCLUDE;

styleSetAttribute: (name=tmplIdOrString ':')? value=styleValue;

styleValue: styleArrayValue | tmplIdOrString | tmplNumberValue | tmplBoolValue;

styleArrayValue: LSQUARE (params+=styleAttribute)? (',' params+=styleAttribute)* RSQUARE;

//tmplStringValue: value=tmplString;

//styleNumberValue: value=NUMBER;

//tmplTextValue: value=tmplText;

//tmplText: TEXT | tmplIntprText;

//tmplIntprText: 's"""' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? '"""';

//styleBoolValue: value= True | False;
