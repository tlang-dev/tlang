parser grammar TLangTmplDoc;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}


tmplDoc: 'doc' (content = tmplDocBlock);

tmplDocBlock: LBRACE START_DOC tmplDocContent END_DOC RBRACE;

tmplDocContent: contents+=tmplDocContentType+;

tmplDocContentType: (tmplDocText);

tmplDocSec: tmplDocSecName = ID LBRACE tmplDocContent RBRACE;

tmplDocStruct: (level = LEVEL1 | LEVEL2 | LEVEL3 | tmplDocAnyLevel) title = ID content = TmplDocContent;

tmplDocText: tmplDocImg | tmplDocLink | tmplDocCodeBlock | tmplDocSpan | tmplDocList | tmplDocTable | tmplDocInclude | PLAIN_TEXT;

tmplDocAnyLevel: LEVEL1 '(' level = NUMBER ')';

tmplDocImg: '[img' src = DOC_STRING (',' alt = STRING)? DOC_RSQUARE;

tmplDocLink: '[link' src = STRING (',' alt = STRING)? RSQUARE;

tmplDocCodeBlock: '[code' lang = STRING LBRACE code = STRING RBRACE RSQUARE;

tmplDocSpan: '[span' content = STRING RSQUARE;

tmplDocList: '[list'  ('type' type = 'bullet' | 'number' ) (content = '*' STRING)* RSQUARE;

tmplDocTable: '[table' (headers += STRING  ('|' headers += STRING)*) RSQUARE;

tmplDocInclude: '[include' src = STRING RSQUARE;
