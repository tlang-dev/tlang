grammar TLangTmplData;

import TLangCommon, TLangHelper, CommonLexer;


tmplData: 'data' content = tmplDataBloc;

tmplDataBloc: ID;
