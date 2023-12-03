parser grammar TLangTmplDoc;

import TLangCommon, TLangHelper;

options {
  tokenVocab = CommonLexer;
}


tmplDoc: DOC (content = tmplDocBlock);

tmplDocBlock: DOC_LBRACE tmplDocContent DOC_RBRACE;

tmplDocContent: contents+=tmplDocContentType+;

tmplDocContentType: tmplDocText | tmplDocStruct | tmplDocSec | tmplDocAsIs;

tmplDocSec: SECTION name = DOC_STRING content = tmplDocContent DOC_RSQUARE;

tmplDocStruct: (level = LEVEL1 | LEVEL2 | LEVEL3 | tmplDocAnyLevel) title = PLAIN_TEXT (content = tmplDocContent)?;

tmplDocText: tmplDocImg | tmplDocLink | tmplDocCodeBlock | tmplDocSpan | tmplDocList | tmplDocTable | tmplDocInclude | tmplDocPlainText;

tmplDocAnyLevel: LEVEL1 '(' level = NUMBER ')';

tmplDocImg: '[img' src = DOC_STRING (alt = DOC_STRING)? DOC_RSQUARE;

tmplDocLink: '[link' src = DOC_STRING name=DOC_STRING DOC_RSQUARE;

tmplDocCodeBlock: '[code' lang = DOC_STRING code = DOC_TEXT DOC_RSQUARE;

tmplDocSpan: '[span' content = STRING RSQUARE;

tmplDocList: '[list'  (order = DOC_STRING ) ((BULLET (contents += tmplDocContent))*) DOC_RSQUARE;

tmplDocTable: '[table' (headers += STRING  ('|' headers += STRING)*) RSQUARE;

tmplDocInclude: '[include' src = STRING RSQUARE;

tmplDocPlainText: PLAIN_TEXT;

tmplDocAsIs: '[asis' content = DOC_TEXT DOC_RSQUARE;
