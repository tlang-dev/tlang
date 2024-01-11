parser grammar TLangTmplData;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}

tmplData: 'data' content = tmplDataBloc;

tmplDataBloc: (name=tmplID)? (LPARENT ((params+=tmplDataAttribute) (',' params+=tmplDataAttribute)*)? RPARENT)?
              	(LBRACE ((attrs+=tmplDataAttribute) (',' attrs+=tmplDataAttribute)*)? RBRACE)?;

tmplDataAttribute: tmplInclude | tmplSetAttribute;

tmplInclude: START_INCLUDE ((calls+=callObj)*) END_INCLUDE;

tmplSetAttribute: (name=tmplIdOrString ':')? value=tmplDataValue;

tmplDataValue: tmplArrayValue | tmplIdOrString | tmplNumberValue | tmplBoolValue;

tmplArrayValue: '[' (params+=tmplDataAttribute)? (',' params+=tmplDataAttribute)* RSQUARE;

//tmplStringValue: value=tmplString;

tmplNumberValue: value=NUMBER;

//tmplTextValue: value=tmplText;

//tmplText: TEXT | tmplIntprText;

//tmplIntprText: 's"""' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? '"""';

tmplBoolValue: value= True | False;

tmplID: ID | tmplIntprID | ESCAPED_ID;

tmplIdOrString: tmplID | tmplString;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? QUOTE;

tmplIntprID: (pre=ID)? INTEPRETED callObj RBRACE (pos=ID)?;
