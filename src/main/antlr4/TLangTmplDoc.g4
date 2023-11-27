grammar TLangTmplDoc;

import TLangCommon, TLangHelper, CommonLexer;


tmplDoc: 'doc' content = tmplDocBlock;

tmplDocBlock: LBRACE tmplDocContent RBRACE;

tmplDocContent: (tmplDocSec | tmplDocStruct | tmplDocText)*;

tmplDocSec: tmplDocSecName = ID LBRACE tmplDocContent RBRACE;

tmplDocStruct: (level = '#' | '##' | '###' | tmplDocAnyLevel) title = ID content = TmplDocContent;

tmplDocText: STRING | tmplDocImg | tmplDocLink | tmplDocCodeBlock | tmplDocSpan | tmplDocList | tmplDocTable | tmplDocInclude;

tmplDocAnyLevel: '#' '(' level = NUMBER ')';

tmplDocImg: '[img' src = STRING (',' alt = STRING)? ']';

tmplDocLink: '[link' src = STRING (',' alt = STRING)? ']';

tmplDocCodeBlock: '[code' lang = STRING LBRACE code = STRING RBRACE ']';

tmplDocSpan: '[span' content = STRING ']';

tmplDocList: '[list'  ('type' type = 'bullet' | ' number' ) (content = '*' STRING)* ']';

tmplDocTable: '[table' (headers += STRING  ('|' headers += STRING)*) ']';

tmplDocInclude: '[include' src = STRING ']';
