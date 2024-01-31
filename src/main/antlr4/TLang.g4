parser grammar TLang;

import TLangModel, TLangHelper, TLangTmplLang, TLangTmplDoc,
TLangTmplData, TLangTmplCmd, TLangTmplStyle;

options {
  tokenVocab = CommonLexer;
}

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

domainUse: Use uses+=ID (PERIOD uses+=ID)? (As alias=ID)?;

domainExpose: Expose expose=ID;

domainBlock: helperBlock | tmplBlock | modelBlock;

/*
 * Tmpl block (Template block)
 * The content of this block will be translated in the final language as it is
 */
tmplBlock: tmplLang | tmplDoc | tmplData | tmplCmd | tmplStyle;
