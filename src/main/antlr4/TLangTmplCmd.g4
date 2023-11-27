grammar TLangTmplCmd;

import TLangCommon, TLangHelper, CommonLexer;


tmplCmd: 'cmd' content = tmplCmdBloc;

tmplCmdBloc: cmd = tmplCmdName;

tmplCmdName: name = ID;

//tmplCmdCallFunc: name = ID '(' args = tmplCmdCallFuncArgs ')';

//tmplCmdCallFuncArgs: args += tmplCmdCallFuncArg (',' args += tmplCmdCallFuncArg)*;

