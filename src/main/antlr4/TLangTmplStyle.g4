parser grammar TLangTmplStyle;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}

tmplStyle: 'style' content=tmplStyleBloc;

tmplStyleBloc: ID;
