parser grammar TLangTmpl;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}

tmplID: ID | tmplIntprID | ESCAPED_ID;

tmplIntprID: (pre=ID)? INTEPRETED callObj RBRACE (pos=ID)?;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? QUOTE;

tmplText: TEXT | tmplIntprText;

tmplIntprText: 's"""' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? '"""';

tmplIdOrString: tmplID | tmplString;

tmplStringValue: value=tmplString;

tmplNumberValue: value=NUMBER;

tmplTextValue: value=tmplText;

tmplBoolValue: value= True | False;
