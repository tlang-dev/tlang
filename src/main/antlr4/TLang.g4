grammar TLang;

import TLangModel, TLangHelper, TLangTmpl, CommonLexer;


/*
 * Domain Model
 * This is the entry point of the language
 *
 */
domainModel:
    header=domainHeader
    body+=domainBlock*;

domainHeader:
    (exposes += domainExpose)*
    (uses += domainUse)*
;

domainUse: 'use' uses+=ID ('.' uses+=ID)? ('as' alias=ID)?;

domainExpose: 'expose' expose=ID;

domainBlock: helperBlock | tmplBlock | modelBlock;
