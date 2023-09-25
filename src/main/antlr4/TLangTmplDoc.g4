grammar TLangTmplDoc;

import TLangCommon, TLangHelper, CommonLexer;


tmplDoc: 'doc' content = tmplDocBloc;

tmplDocBloc: LBRACE tmplDocContent RBRACE;

tmplDocContent: STRING;