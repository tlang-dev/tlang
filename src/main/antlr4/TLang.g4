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

domainUse: Use uses+=ID ('.' uses+=ID)? (As alias=ID)?;

domainExpose: Expose expose=ID;

domainBlock: helperBlock | tmplBlock | modelBlock;
