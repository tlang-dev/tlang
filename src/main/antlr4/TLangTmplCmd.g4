parser grammar TLangTmplCmd;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}

tmplCmd: CMD LSQUARE (langs+=ID (',' langs+=ID)*) RSQUARE name=ID LPARENT (params += helperParam (',' params += helperParam)*)?RPARENT  LBRACE  content = tmplCmdBlock RBRACE;

tmplCmdBlock: cmd = tmplCmdName | tmplCmdCallFunc;

tmplCmdName: name = ID;

tmplCmdCallFunc: name = ID '(' args = tmplCmdCallFuncArgs ')';

tmplCmdCallFuncArgs: args += tmplCmdCallFuncArg (',' args += tmplCmdCallFuncArg)*;

tmplCmdCallFuncArg: (name=tmplIdOrString ':')? value=tmplID;

tmplIdOrString: tmplID | tmplString;

tmplID: ID | tmplIntprID | ESCAPED_ID;

tmplIntprID: (pre=ID)? INTEPRETED callObj RBRACE (pos=ID)?;

tmplString: STRING | tmplIntprString;

tmplIntprString: 's"' (pre=.)? INTEPRETED callObj RBRACE (pos=.)? QUOTE;
