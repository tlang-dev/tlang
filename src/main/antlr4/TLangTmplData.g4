parser grammar TLangTmplData;

import TLangCommon, TLangHelper;


options {
  tokenVocab = CommonLexer;
}

tmplData: 'data' content = tmplDataBloc;

tmplDataBloc: content= ID ;
