// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLangParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, LANG=21, FILE=22, HELPER=23, FUNC=24, TEXT=25, 
		ANY_ID=26, ID=27, ID_RPL=28, WS=29, ESCAPED_QUOTE=30, STRING=31, NUMBER=32, 
		MODEL=33;
	public static final int
		RULE_domainModel = 0, RULE_lang = 1, RULE_file = 2, RULE_helperBlock = 3, 
		RULE_helperFunc = 4, RULE_tmplBlock = 5, RULE_tmplPkg = 6, RULE_tmplUse = 7, 
		RULE_tmplImpl = 8, RULE_tmplImplContent = 9, RULE_tmplFunc = 10, RULE_tmplCurrying = 11, 
		RULE_tmplCurryingParam = 12, RULE_tmplParam = 13, RULE_tmplType = 14, 
		RULE_tmplGeneric = 15, RULE_tmplExpression = 16, RULE_tmplVal = 17, RULE_tmplVar = 18, 
		RULE_modelBlock = 19, RULE_modelContent = 20, RULE_modelNewEntity = 21, 
		RULE_modelValueType = 22, RULE_modelTbl = 23, RULE_modelEntityAsAttribut = 24, 
		RULE_modelAttribut = 25, RULE_modelSetEntity = 26, RULE_modelSetAttribute = 27, 
		RULE_modelSetValueType = 28, RULE_modelSetType = 29, RULE_modelSetFuncDef = 30, 
		RULE_modelSetRef = 31;
	private static String[] makeRuleNames() {
		return new String[] {
			"domainModel", "lang", "file", "helperBlock", "helperFunc", "tmplBlock", 
			"tmplPkg", "tmplUse", "tmplImpl", "tmplImplContent", "tmplFunc", "tmplCurrying", 
			"tmplCurryingParam", "tmplParam", "tmplType", "tmplGeneric", "tmplExpression", 
			"tmplVal", "tmplVar", "modelBlock", "modelContent", "modelNewEntity", 
			"modelValueType", "modelTbl", "modelEntityAsAttribut", "modelAttribut", 
			"modelSetEntity", "modelSetAttribute", "modelSetValueType", "modelSetType", 
			"modelSetFuncDef", "modelSetRef"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "'tmpl'", "'pkg'", "'use'", "'impl'", "'for'", "','", 
			"':'", "'('", "')'", "'<'", "'>'", "'['", "']'", "'val'", "'='", "'var'", 
			"'set'", "'->'", "'lang'", "'file'", "'helper'", "'func'", null, null, 
			null, null, null, "'\\\"'", null, null, "'model'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "LANG", "FILE", 
			"HELPER", "FUNC", "TEXT", "ANY_ID", "ID", "ID_RPL", "WS", "ESCAPED_QUOTE", 
			"STRING", "NUMBER", "MODEL"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "TLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TLangParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class DomainModelContext extends ParserRuleContext {
		public HelperBlockContext helperBlock;
		public List<HelperBlockContext> helperBlocks = new ArrayList<HelperBlockContext>();
		public TmplBlockContext tmplBlock;
		public List<TmplBlockContext> tmplBlocks = new ArrayList<TmplBlockContext>();
		public ModelBlockContext modelBlock;
		public List<ModelBlockContext> modelBlocks = new ArrayList<ModelBlockContext>();
		public LangContext lang() {
			return getRuleContext(LangContext.class,0);
		}
		public FileContext file() {
			return getRuleContext(FileContext.class,0);
		}
		public List<HelperBlockContext> helperBlock() {
			return getRuleContexts(HelperBlockContext.class);
		}
		public HelperBlockContext helperBlock(int i) {
			return getRuleContext(HelperBlockContext.class,i);
		}
		public List<TmplBlockContext> tmplBlock() {
			return getRuleContexts(TmplBlockContext.class);
		}
		public TmplBlockContext tmplBlock(int i) {
			return getRuleContext(TmplBlockContext.class,i);
		}
		public List<ModelBlockContext> modelBlock() {
			return getRuleContexts(ModelBlockContext.class);
		}
		public ModelBlockContext modelBlock(int i) {
			return getRuleContext(ModelBlockContext.class,i);
		}
		public DomainModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domainModel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterDomainModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitDomainModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitDomainModel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DomainModelContext domainModel() throws RecognitionException {
		DomainModelContext _localctx = new DomainModelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_domainModel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LANG) {
				{
				setState(64);
				lang();
				}
			}

			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FILE) {
				{
				setState(67);
				file();
				}
			}

			setState(73);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HELPER) {
				{
				{
				setState(70);
				((DomainModelContext)_localctx).helperBlock = helperBlock();
				((DomainModelContext)_localctx).helperBlocks.add(((DomainModelContext)_localctx).helperBlock);
				}
				}
				setState(75);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(76);
				((DomainModelContext)_localctx).tmplBlock = tmplBlock();
				((DomainModelContext)_localctx).tmplBlocks.add(((DomainModelContext)_localctx).tmplBlock);
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MODEL) {
				{
				{
				setState(82);
				((DomainModelContext)_localctx).modelBlock = modelBlock();
				((DomainModelContext)_localctx).modelBlocks.add(((DomainModelContext)_localctx).modelBlock);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LangContext extends ParserRuleContext {
		public Token name;
		public TerminalNode LANG() { return getToken(TLangParser.LANG, 0); }
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public LangContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_lang; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterLang(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitLang(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitLang(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LangContext lang() throws RecognitionException {
		LangContext _localctx = new LangContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_lang);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(88);
			match(LANG);
			setState(89);
			((LangContext)_localctx).name = match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FileContext extends ParserRuleContext {
		public Token name;
		public TerminalNode FILE() { return getToken(TLangParser.FILE, 0); }
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public FileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_file; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitFile(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FileContext file() throws RecognitionException {
		FileContext _localctx = new FileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_file);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91);
			match(FILE);
			setState(92);
			((FileContext)_localctx).name = match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HelperBlockContext extends ParserRuleContext {
		public HelperFuncContext helperFunc;
		public List<HelperFuncContext> helperFuncs = new ArrayList<HelperFuncContext>();
		public TerminalNode HELPER() { return getToken(TLangParser.HELPER, 0); }
		public List<HelperFuncContext> helperFunc() {
			return getRuleContexts(HelperFuncContext.class);
		}
		public HelperFuncContext helperFunc(int i) {
			return getRuleContext(HelperFuncContext.class,i);
		}
		public HelperBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperBlockContext helperBlock() throws RecognitionException {
		HelperBlockContext _localctx = new HelperBlockContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_helperBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			match(HELPER);
			setState(95);
			match(T__0);
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FUNC) {
				{
				{
				setState(96);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class HelperFuncContext extends ParserRuleContext {
		public Token name;
		public TerminalNode FUNC() { return getToken(TLangParser.FUNC, 0); }
		public TerminalNode ANY_ID() { return getToken(TLangParser.ANY_ID, 0); }
		public HelperFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperFuncContext helperFunc() throws RecognitionException {
		HelperFuncContext _localctx = new HelperFuncContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_helperFunc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
			match(FUNC);
			setState(105);
			((HelperFuncContext)_localctx).name = match(ANY_ID);
			setState(106);
			match(T__0);
			setState(107);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplBlockContext extends ParserRuleContext {
		public TmplPkgContext tmplPakage;
		public TmplUseContext tmplUse;
		public List<TmplUseContext> tmplUses = new ArrayList<TmplUseContext>();
		public TmplImplContext tmplImpl;
		public List<TmplImplContext> tmplImpls = new ArrayList<TmplImplContext>();
		public TmplFuncContext tmplFunc;
		public List<TmplFuncContext> tmplFuncs = new ArrayList<TmplFuncContext>();
		public TmplPkgContext tmplPkg() {
			return getRuleContext(TmplPkgContext.class,0);
		}
		public List<TmplUseContext> tmplUse() {
			return getRuleContexts(TmplUseContext.class);
		}
		public TmplUseContext tmplUse(int i) {
			return getRuleContext(TmplUseContext.class,i);
		}
		public List<TmplImplContext> tmplImpl() {
			return getRuleContexts(TmplImplContext.class);
		}
		public TmplImplContext tmplImpl(int i) {
			return getRuleContext(TmplImplContext.class,i);
		}
		public List<TmplFuncContext> tmplFunc() {
			return getRuleContexts(TmplFuncContext.class);
		}
		public TmplFuncContext tmplFunc(int i) {
			return getRuleContext(TmplFuncContext.class,i);
		}
		public TmplBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplBlockContext tmplBlock() throws RecognitionException {
		TmplBlockContext _localctx = new TmplBlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_tmplBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(T__2);
			setState(110);
			match(T__0);
			setState(112);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(111);
				((TmplBlockContext)_localctx).tmplPakage = tmplPkg();
				}
			}

			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(114);
				((TmplBlockContext)_localctx).tmplUse = tmplUse();
				((TmplBlockContext)_localctx).tmplUses.add(((TmplBlockContext)_localctx).tmplUse);
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(120);
				((TmplBlockContext)_localctx).tmplImpl = tmplImpl();
				((TmplBlockContext)_localctx).tmplImpls.add(((TmplBlockContext)_localctx).tmplImpl);
				}
				}
				setState(125);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FUNC) {
				{
				{
				setState(126);
				((TmplBlockContext)_localctx).tmplFunc = tmplFunc();
				((TmplBlockContext)_localctx).tmplFuncs.add(((TmplBlockContext)_localctx).tmplFunc);
				}
				}
				setState(131);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(132);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplPkgContext extends ParserRuleContext {
		public Token name;
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public TmplPkgContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplPkg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplPkg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplPkg(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplPkg(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplPkgContext tmplPkg() throws RecognitionException {
		TmplPkgContext _localctx = new TmplPkgContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_tmplPkg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			match(T__3);
			setState(135);
			((TmplPkgContext)_localctx).name = match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplUseContext extends ParserRuleContext {
		public Token name;
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public TmplUseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplUse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplUse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplUse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplUse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplUseContext tmplUse() throws RecognitionException {
		TmplUseContext _localctx = new TmplUseContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_tmplUse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(137);
			match(T__4);
			setState(138);
			((TmplUseContext)_localctx).name = match(STRING);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplImplContext extends ParserRuleContext {
		public Token name;
		public Token forName;
		public Token ANY_ID;
		public List<Token> forNames = new ArrayList<Token>();
		public TmplImplContentContext tmplImplContent;
		public List<TmplImplContentContext> tmplImplContents = new ArrayList<TmplImplContentContext>();
		public List<TerminalNode> ANY_ID() { return getTokens(TLangParser.ANY_ID); }
		public TerminalNode ANY_ID(int i) {
			return getToken(TLangParser.ANY_ID, i);
		}
		public List<TmplImplContentContext> tmplImplContent() {
			return getRuleContexts(TmplImplContentContext.class);
		}
		public TmplImplContentContext tmplImplContent(int i) {
			return getRuleContext(TmplImplContentContext.class,i);
		}
		public TmplImplContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplImpl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplImpl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplImpl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplImpl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplImplContext tmplImpl() throws RecognitionException {
		TmplImplContext _localctx = new TmplImplContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_tmplImpl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(140);
			match(T__5);
			setState(141);
			((TmplImplContext)_localctx).name = match(ANY_ID);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				{
				setState(142);
				match(T__6);
				setState(143);
				((TmplImplContext)_localctx).forName = match(ANY_ID);
				}
				setState(149);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(145);
					match(T__7);
					setState(146);
					((TmplImplContext)_localctx).ANY_ID = match(ANY_ID);
					((TmplImplContext)_localctx).forNames.add(((TmplImplContext)_localctx).ANY_ID);
					}
					}
					setState(151);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(154);
			match(T__0);
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__17) | (1L << FUNC))) != 0)) {
				{
				{
				setState(155);
				((TmplImplContext)_localctx).tmplImplContent = tmplImplContent();
				((TmplImplContext)_localctx).tmplImplContents.add(((TmplImplContext)_localctx).tmplImplContent);
				}
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(161);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplImplContentContext extends ParserRuleContext {
		public TmplExpressionContext tmplExpression() {
			return getRuleContext(TmplExpressionContext.class,0);
		}
		public TmplFuncContext tmplFunc() {
			return getRuleContext(TmplFuncContext.class,0);
		}
		public TmplImplContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplImplContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplImplContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplImplContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplImplContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplImplContentContext tmplImplContent() throws RecognitionException {
		TmplImplContentContext _localctx = new TmplImplContentContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_tmplImplContent);
		try {
			setState(165);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				tmplExpression();
				}
				break;
			case FUNC:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				tmplFunc();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplFuncContext extends ParserRuleContext {
		public Token name;
		public TmplCurryingContext tmplCurrying;
		public List<TmplCurryingContext> curries = new ArrayList<TmplCurryingContext>();
		public TmplTypeContext tmplType;
		public List<TmplTypeContext> types = new ArrayList<TmplTypeContext>();
		public TmplExpressionContext tmplExpression;
		public List<TmplExpressionContext> exprs = new ArrayList<TmplExpressionContext>();
		public TerminalNode FUNC() { return getToken(TLangParser.FUNC, 0); }
		public TerminalNode ANY_ID() { return getToken(TLangParser.ANY_ID, 0); }
		public List<TmplCurryingContext> tmplCurrying() {
			return getRuleContexts(TmplCurryingContext.class);
		}
		public TmplCurryingContext tmplCurrying(int i) {
			return getRuleContext(TmplCurryingContext.class,i);
		}
		public List<TmplTypeContext> tmplType() {
			return getRuleContexts(TmplTypeContext.class);
		}
		public TmplTypeContext tmplType(int i) {
			return getRuleContext(TmplTypeContext.class,i);
		}
		public List<TmplExpressionContext> tmplExpression() {
			return getRuleContexts(TmplExpressionContext.class);
		}
		public TmplExpressionContext tmplExpression(int i) {
			return getRuleContext(TmplExpressionContext.class,i);
		}
		public TmplFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplFuncContext tmplFunc() throws RecognitionException {
		TmplFuncContext _localctx = new TmplFuncContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_tmplFunc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(167);
			match(FUNC);
			setState(168);
			((TmplFuncContext)_localctx).name = match(ANY_ID);
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(169);
				((TmplFuncContext)_localctx).tmplCurrying = tmplCurrying();
				((TmplFuncContext)_localctx).curries.add(((TmplFuncContext)_localctx).tmplCurrying);
				}
				}
				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(184);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(175);
				match(T__8);
				setState(176);
				((TmplFuncContext)_localctx).tmplType = tmplType();
				((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
				setState(181);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(177);
					match(T__7);
					setState(178);
					((TmplFuncContext)_localctx).tmplType = tmplType();
					((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
					}
					}
					setState(183);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(186);
				match(T__0);
				setState(190);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15 || _la==T__17) {
					{
					{
					setState(187);
					((TmplFuncContext)_localctx).tmplExpression = tmplExpression();
					((TmplFuncContext)_localctx).exprs.add(((TmplFuncContext)_localctx).tmplExpression);
					}
					}
					setState(192);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(193);
				match(T__1);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplCurryingContext extends ParserRuleContext {
		public TmplCurryingParamContext param;
		public TmplCurryingParamContext tmplCurryingParam() {
			return getRuleContext(TmplCurryingParamContext.class,0);
		}
		public TmplCurryingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplCurrying; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplCurrying(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplCurrying(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplCurrying(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplCurryingContext tmplCurrying() throws RecognitionException {
		TmplCurryingContext _localctx = new TmplCurryingContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_tmplCurrying);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(196);
			match(T__9);
			setState(197);
			((TmplCurryingContext)_localctx).param = tmplCurryingParam();
			setState(198);
			match(T__10);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplCurryingParamContext extends ParserRuleContext {
		public TmplParamContext tmplParam;
		public List<TmplParamContext> params = new ArrayList<TmplParamContext>();
		public List<TmplParamContext> tmplParam() {
			return getRuleContexts(TmplParamContext.class);
		}
		public TmplParamContext tmplParam(int i) {
			return getRuleContext(TmplParamContext.class,i);
		}
		public TmplCurryingParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplCurryingParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplCurryingParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplCurryingParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplCurryingParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplCurryingParamContext tmplCurryingParam() throws RecognitionException {
		TmplCurryingParamContext _localctx = new TmplCurryingParamContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_tmplCurryingParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(208);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ANY_ID) {
				{
				{
				setState(200);
				((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
				((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
				}
				setState(205);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(201);
					match(T__7);
					setState(202);
					((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
					((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
					}
					}
					setState(207);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplParamContext extends ParserRuleContext {
		public Token accessor;
		public Token name;
		public TmplTypeContext type;
		public List<TerminalNode> ANY_ID() { return getTokens(TLangParser.ANY_ID); }
		public TerminalNode ANY_ID(int i) {
			return getToken(TLangParser.ANY_ID, i);
		}
		public TmplTypeContext tmplType() {
			return getRuleContext(TmplTypeContext.class,0);
		}
		public TmplParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplParamContext tmplParam() throws RecognitionException {
		TmplParamContext _localctx = new TmplParamContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_tmplParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(210);
				((TmplParamContext)_localctx).accessor = match(ANY_ID);
				}
				break;
			}
			setState(213);
			((TmplParamContext)_localctx).name = match(ANY_ID);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(214);
				match(T__8);
				setState(215);
				((TmplParamContext)_localctx).type = tmplType();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplTypeContext extends ParserRuleContext {
		public Token type;
		public TmplGenericContext generic;
		public Token array;
		public TerminalNode ANY_ID() { return getToken(TLangParser.ANY_ID, 0); }
		public TmplGenericContext tmplGeneric() {
			return getRuleContext(TmplGenericContext.class,0);
		}
		public TmplTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplTypeContext tmplType() throws RecognitionException {
		TmplTypeContext _localctx = new TmplTypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_tmplType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(218);
			((TmplTypeContext)_localctx).type = match(ANY_ID);
			setState(223);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(219);
				match(T__11);
				{
				setState(220);
				((TmplTypeContext)_localctx).generic = tmplGeneric();
				}
				setState(221);
				match(T__12);
				}
			}

			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(225);
				((TmplTypeContext)_localctx).array = match(T__13);
				setState(226);
				match(T__14);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplGenericContext extends ParserRuleContext {
		public TmplTypeContext tmplType;
		public List<TmplTypeContext> types = new ArrayList<TmplTypeContext>();
		public List<TmplTypeContext> tmplType() {
			return getRuleContexts(TmplTypeContext.class);
		}
		public TmplTypeContext tmplType(int i) {
			return getRuleContext(TmplTypeContext.class,i);
		}
		public TmplGenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplGeneric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplGeneric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplGeneric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplGenericContext tmplGeneric() throws RecognitionException {
		TmplGenericContext _localctx = new TmplGenericContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_tmplGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(229);
			((TmplGenericContext)_localctx).tmplType = tmplType();
			((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
			setState(234);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(230);
				match(T__7);
				setState(231);
				((TmplGenericContext)_localctx).tmplType = tmplType();
				((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
				}
				}
				setState(236);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplExpressionContext extends ParserRuleContext {
		public TmplValContext tmplVal() {
			return getRuleContext(TmplValContext.class,0);
		}
		public TmplVarContext tmplVar() {
			return getRuleContext(TmplVarContext.class,0);
		}
		public TmplExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplExpressionContext tmplExpression() throws RecognitionException {
		TmplExpressionContext _localctx = new TmplExpressionContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_tmplExpression);
		try {
			setState(239);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(237);
				tmplVal();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 2);
				{
				setState(238);
				tmplVar();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplValContext extends ParserRuleContext {
		public Token name;
		public TmplTypeContext type;
		public TmplExpressionContext value;
		public TerminalNode ANY_ID() { return getToken(TLangParser.ANY_ID, 0); }
		public TmplTypeContext tmplType() {
			return getRuleContext(TmplTypeContext.class,0);
		}
		public TmplExpressionContext tmplExpression() {
			return getRuleContext(TmplExpressionContext.class,0);
		}
		public TmplValContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplVal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplVal(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplVal(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplVal(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplValContext tmplVal() throws RecognitionException {
		TmplValContext _localctx = new TmplValContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_tmplVal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			match(T__15);
			setState(242);
			((TmplValContext)_localctx).name = match(ANY_ID);
			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(243);
				match(T__8);
				setState(244);
				((TmplValContext)_localctx).type = tmplType();
				}
			}

			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(247);
				match(T__16);
				setState(248);
				((TmplValContext)_localctx).value = tmplExpression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class TmplVarContext extends ParserRuleContext {
		public Token name;
		public TmplTypeContext type;
		public TmplExpressionContext value;
		public TerminalNode ANY_ID() { return getToken(TLangParser.ANY_ID, 0); }
		public TmplTypeContext tmplType() {
			return getRuleContext(TmplTypeContext.class,0);
		}
		public TmplExpressionContext tmplExpression() {
			return getRuleContext(TmplExpressionContext.class,0);
		}
		public TmplVarContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tmplVar; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterTmplVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitTmplVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitTmplVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TmplVarContext tmplVar() throws RecognitionException {
		TmplVarContext _localctx = new TmplVarContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_tmplVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(251);
			match(T__17);
			setState(252);
			((TmplVarContext)_localctx).name = match(ANY_ID);
			setState(255);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(253);
				match(T__8);
				setState(254);
				((TmplVarContext)_localctx).type = tmplType();
				}
			}

			setState(259);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(257);
				match(T__16);
				setState(258);
				((TmplVarContext)_localctx).value = tmplExpression();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelBlockContext extends ParserRuleContext {
		public ModelContentContext modelContent;
		public List<ModelContentContext> modelContents = new ArrayList<ModelContentContext>();
		public TerminalNode MODEL() { return getToken(TLangParser.MODEL, 0); }
		public List<ModelContentContext> modelContent() {
			return getRuleContexts(ModelContentContext.class);
		}
		public ModelContentContext modelContent(int i) {
			return getRuleContext(ModelContentContext.class,i);
		}
		public ModelBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelBlockContext modelBlock() throws RecognitionException {
		ModelBlockContext _localctx = new ModelBlockContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_modelBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(MODEL);
			setState(262);
			match(T__0);
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__18 || _la==ID) {
				{
				{
				setState(263);
				((ModelBlockContext)_localctx).modelContent = modelContent();
				((ModelBlockContext)_localctx).modelContents.add(((ModelBlockContext)_localctx).modelContent);
				}
				}
				setState(268);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(269);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelContentContext extends ParserRuleContext {
		public ModelNewEntityContext modelNewEntity() {
			return getRuleContext(ModelNewEntityContext.class,0);
		}
		public ModelSetEntityContext modelSetEntity() {
			return getRuleContext(ModelSetEntityContext.class,0);
		}
		public ModelContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelContentContext modelContent() throws RecognitionException {
		ModelContentContext _localctx = new ModelContentContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_modelContent);
		try {
			setState(273);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(271);
				modelNewEntity();
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 2);
				{
				setState(272);
				modelSetEntity();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelNewEntityContext extends ParserRuleContext {
		public Token type;
		public ModelAttributContext modelAttribut;
		public List<ModelAttributContext> attrs = new ArrayList<ModelAttributContext>();
		public ModelValueTypeContext modelValueType;
		public List<ModelValueTypeContext> decl = new ArrayList<ModelValueTypeContext>();
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public List<ModelValueTypeContext> modelValueType() {
			return getRuleContexts(ModelValueTypeContext.class);
		}
		public ModelValueTypeContext modelValueType(int i) {
			return getRuleContext(ModelValueTypeContext.class,i);
		}
		public List<ModelAttributContext> modelAttribut() {
			return getRuleContexts(ModelAttributContext.class);
		}
		public ModelAttributContext modelAttribut(int i) {
			return getRuleContext(ModelAttributContext.class,i);
		}
		public ModelNewEntityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelNewEntity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelNewEntity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelNewEntity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelNewEntity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelNewEntityContext modelNewEntity() throws RecognitionException {
		ModelNewEntityContext _localctx = new ModelNewEntityContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_modelNewEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			((ModelNewEntityContext)_localctx).type = match(ID);
			setState(287);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(276);
				match(T__9);
				{
				{
				setState(277);
				((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
				((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
				}
				setState(282);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(278);
					match(T__7);
					setState(279);
					((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
					((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
					}
					}
					setState(284);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(285);
				match(T__10);
				}
			}

			setState(289);
			match(T__0);
			setState(293);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << ID) | (1L << STRING))) != 0)) {
				{
				{
				setState(290);
				((ModelNewEntityContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityContext)_localctx).decl.add(((ModelNewEntityContext)_localctx).modelValueType);
				}
				}
				setState(295);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(296);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelValueTypeContext extends ParserRuleContext {
		public ModelAttributContext modelAttribut() {
			return getRuleContext(ModelAttributContext.class,0);
		}
		public ModelEntityAsAttributContext modelEntityAsAttribut() {
			return getRuleContext(ModelEntityAsAttributContext.class,0);
		}
		public ModelTblContext modelTbl() {
			return getRuleContext(ModelTblContext.class,0);
		}
		public ModelValueTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelValueType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelValueType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelValueType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelValueType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelValueTypeContext modelValueType() throws RecognitionException {
		ModelValueTypeContext _localctx = new ModelValueTypeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_modelValueType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(301);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(298);
				modelAttribut();
				}
				break;
			case 2:
				{
				setState(299);
				modelEntityAsAttribut();
				}
				break;
			case 3:
				{
				setState(300);
				modelTbl();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelTblContext extends ParserRuleContext {
		public Token attr;
		public ModelValueTypeContext modelValueType;
		public List<ModelValueTypeContext> elms = new ArrayList<ModelValueTypeContext>();
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public List<ModelValueTypeContext> modelValueType() {
			return getRuleContexts(ModelValueTypeContext.class);
		}
		public ModelValueTypeContext modelValueType(int i) {
			return getRuleContext(ModelValueTypeContext.class,i);
		}
		public ModelTblContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelTbl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelTbl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelTbl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelTbl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelTblContext modelTbl() throws RecognitionException {
		ModelTblContext _localctx = new ModelTblContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_modelTbl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(304);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(303);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(306);
			match(T__13);
			}
			{
			{
			setState(307);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(312);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(308);
				match(T__7);
				setState(309);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(314);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(315);
			match(T__14);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelEntityAsAttributContext extends ParserRuleContext {
		public Token attr;
		public ModelNewEntityContext value;
		public ModelNewEntityContext modelNewEntity() {
			return getRuleContext(ModelNewEntityContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelEntityAsAttributContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelEntityAsAttribut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelEntityAsAttribut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelEntityAsAttribut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelEntityAsAttribut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelEntityAsAttributContext modelEntityAsAttribut() throws RecognitionException {
		ModelEntityAsAttributContext _localctx = new ModelEntityAsAttributContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_modelEntityAsAttribut);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(318);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,39,_ctx) ) {
			case 1:
				{
				setState(317);
				((ModelEntityAsAttributContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(320);
			((ModelEntityAsAttributContext)_localctx).value = modelNewEntity();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelAttributContext extends ParserRuleContext {
		public Token attr;
		public Token value;
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelAttributContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelAttribut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelAttribut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelAttribut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelAttribut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelAttributContext modelAttribut() throws RecognitionException {
		ModelAttributContext _localctx = new ModelAttributContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_modelAttribut);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(322);
				((ModelAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(325);
			((ModelAttributContext)_localctx).value = match(STRING);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetEntityContext extends ParserRuleContext {
		public Token name;
		public ModelSetAttributeContext modelSetAttribute;
		public List<ModelSetAttributeContext> params = new ArrayList<ModelSetAttributeContext>();
		public List<ModelSetAttributeContext> attrs = new ArrayList<ModelSetAttributeContext>();
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public List<ModelSetAttributeContext> modelSetAttribute() {
			return getRuleContexts(ModelSetAttributeContext.class);
		}
		public ModelSetAttributeContext modelSetAttribute(int i) {
			return getRuleContext(ModelSetAttributeContext.class,i);
		}
		public ModelSetEntityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetEntity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetEntity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetEntity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetEntity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetEntityContext modelSetEntity() throws RecognitionException {
		ModelSetEntityContext _localctx = new ModelSetEntityContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_modelSetEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(327);
			match(T__18);
			setState(328);
			((ModelSetEntityContext)_localctx).name = match(ID);
			setState(340);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(329);
				match(T__9);
				{
				{
				setState(330);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				setState(335);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(331);
					match(T__7);
					setState(332);
					((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
					((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
					}
					}
					setState(337);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(338);
				match(T__10);
				}
			}

			setState(342);
			match(T__0);
			setState(346);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << T__19) | (1L << ID))) != 0)) {
				{
				{
				setState(343);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).attrs.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				}
				setState(348);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(349);
			match(T__1);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetAttributeContext extends ParserRuleContext {
		public Token attr;
		public ModelSetValueTypeContext value;
		public ModelSetValueTypeContext modelSetValueType() {
			return getRuleContext(ModelSetValueTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelSetAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetAttributeContext modelSetAttribute() throws RecognitionException {
		ModelSetAttributeContext _localctx = new ModelSetAttributeContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_modelSetAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(352);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(351);
				((ModelSetAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(354);
			((ModelSetAttributeContext)_localctx).value = modelSetValueType();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetValueTypeContext extends ParserRuleContext {
		public ModelSetTypeContext modelSetType() {
			return getRuleContext(ModelSetTypeContext.class,0);
		}
		public ModelSetFuncDefContext modelSetFuncDef() {
			return getRuleContext(ModelSetFuncDefContext.class,0);
		}
		public ModelSetRefContext modelSetRef() {
			return getRuleContext(ModelSetRefContext.class,0);
		}
		public ModelSetValueTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetValueType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetValueType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetValueType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetValueType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetValueTypeContext modelSetValueType() throws RecognitionException {
		ModelSetValueTypeContext _localctx = new ModelSetValueTypeContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_modelSetValueType);
		try {
			setState(359);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(356);
				modelSetType();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(357);
				modelSetFuncDef();
				}
				break;
			case T__19:
				enterOuterAlt(_localctx, 3);
				{
				setState(358);
				modelSetRef();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetTypeContext extends ParserRuleContext {
		public Token type;
		public TmplGenericContext generic;
		public Token array;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public TmplGenericContext tmplGeneric() {
			return getRuleContext(TmplGenericContext.class,0);
		}
		public ModelSetTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetTypeContext modelSetType() throws RecognitionException {
		ModelSetTypeContext _localctx = new ModelSetTypeContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_modelSetType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(361);
			((ModelSetTypeContext)_localctx).type = match(ID);
			setState(366);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(362);
				match(T__11);
				{
				setState(363);
				((ModelSetTypeContext)_localctx).generic = tmplGeneric();
				}
				setState(364);
				match(T__12);
				}
			}

			setState(370);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(368);
				((ModelSetTypeContext)_localctx).array = match(T__13);
				setState(369);
				match(T__14);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetFuncDefContext extends ParserRuleContext {
		public ModelSetTypeContext modelSetType;
		public List<ModelSetTypeContext> retTypes = new ArrayList<ModelSetTypeContext>();
		public List<ModelSetTypeContext> modelSetType() {
			return getRuleContexts(ModelSetTypeContext.class);
		}
		public ModelSetTypeContext modelSetType(int i) {
			return getRuleContext(ModelSetTypeContext.class,i);
		}
		public ModelSetFuncDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetFuncDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetFuncDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetFuncDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetFuncDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetFuncDefContext modelSetFuncDef() throws RecognitionException {
		ModelSetFuncDefContext _localctx = new ModelSetFuncDefContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_modelSetFuncDef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(372);
			match(T__9);
			setState(373);
			match(T__10);
			setState(383);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,49,_ctx) ) {
			case 1:
				{
				setState(374);
				match(T__19);
				setState(375);
				((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
				((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
				setState(380);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(376);
						match(T__7);
						setState(377);
						((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
						((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
						}
						} 
					}
					setState(382);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,48,_ctx);
				}
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModelSetRefContext extends ParserRuleContext {
		public Token ref;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelSetRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetRef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelSetRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelSetRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelSetRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetRefContext modelSetRef() throws RecognitionException {
		ModelSetRefContext _localctx = new ModelSetRefContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_modelSetRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(T__19);
			setState(386);
			((ModelSetRefContext)_localctx).ref = match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3#\u0187\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\3\2\5\2D\n\2\3\2\5\2G\n\2\3\2\7\2J\n\2\f\2\16\2M\13\2\3\2\7\2P\n\2"+
		"\f\2\16\2S\13\2\3\2\7\2V\n\2\f\2\16\2Y\13\2\3\3\3\3\3\3\3\4\3\4\3\4\3"+
		"\5\3\5\3\5\7\5d\n\5\f\5\16\5g\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7"+
		"\3\7\5\7s\n\7\3\7\7\7v\n\7\f\7\16\7y\13\7\3\7\7\7|\n\7\f\7\16\7\177\13"+
		"\7\3\7\7\7\u0082\n\7\f\7\16\7\u0085\13\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3"+
		"\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0096\n\n\f\n\16\n\u0099\13\n\5\n\u009b"+
		"\n\n\3\n\3\n\7\n\u009f\n\n\f\n\16\n\u00a2\13\n\3\n\3\n\3\13\3\13\5\13"+
		"\u00a8\n\13\3\f\3\f\3\f\7\f\u00ad\n\f\f\f\16\f\u00b0\13\f\3\f\3\f\3\f"+
		"\3\f\7\f\u00b6\n\f\f\f\16\f\u00b9\13\f\5\f\u00bb\n\f\3\f\3\f\7\f\u00bf"+
		"\n\f\f\f\16\f\u00c2\13\f\3\f\5\f\u00c5\n\f\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\7\16\u00ce\n\16\f\16\16\16\u00d1\13\16\5\16\u00d3\n\16\3\17\5\17\u00d6"+
		"\n\17\3\17\3\17\3\17\5\17\u00db\n\17\3\20\3\20\3\20\3\20\3\20\5\20\u00e2"+
		"\n\20\3\20\3\20\5\20\u00e6\n\20\3\21\3\21\3\21\7\21\u00eb\n\21\f\21\16"+
		"\21\u00ee\13\21\3\22\3\22\5\22\u00f2\n\22\3\23\3\23\3\23\3\23\5\23\u00f8"+
		"\n\23\3\23\3\23\5\23\u00fc\n\23\3\24\3\24\3\24\3\24\5\24\u0102\n\24\3"+
		"\24\3\24\5\24\u0106\n\24\3\25\3\25\3\25\7\25\u010b\n\25\f\25\16\25\u010e"+
		"\13\25\3\25\3\25\3\26\3\26\5\26\u0114\n\26\3\27\3\27\3\27\3\27\3\27\7"+
		"\27\u011b\n\27\f\27\16\27\u011e\13\27\3\27\3\27\5\27\u0122\n\27\3\27\3"+
		"\27\7\27\u0126\n\27\f\27\16\27\u0129\13\27\3\27\3\27\3\30\3\30\3\30\5"+
		"\30\u0130\n\30\3\31\5\31\u0133\n\31\3\31\3\31\3\31\3\31\7\31\u0139\n\31"+
		"\f\31\16\31\u013c\13\31\3\31\3\31\3\32\5\32\u0141\n\32\3\32\3\32\3\33"+
		"\5\33\u0146\n\33\3\33\3\33\3\34\3\34\3\34\3\34\3\34\3\34\7\34\u0150\n"+
		"\34\f\34\16\34\u0153\13\34\3\34\3\34\5\34\u0157\n\34\3\34\3\34\7\34\u015b"+
		"\n\34\f\34\16\34\u015e\13\34\3\34\3\34\3\35\5\35\u0163\n\35\3\35\3\35"+
		"\3\36\3\36\3\36\5\36\u016a\n\36\3\37\3\37\3\37\3\37\3\37\5\37\u0171\n"+
		"\37\3\37\3\37\5\37\u0175\n\37\3 \3 \3 \3 \3 \3 \7 \u017d\n \f \16 \u0180"+
		"\13 \5 \u0182\n \3!\3!\3!\3!\2\2\"\2\4\6\b\n\f\16\20\22\24\26\30\32\34"+
		"\36 \"$&(*,.\60\62\64\668:<>@\2\2\2\u019a\2C\3\2\2\2\4Z\3\2\2\2\6]\3\2"+
		"\2\2\b`\3\2\2\2\nj\3\2\2\2\fo\3\2\2\2\16\u0088\3\2\2\2\20\u008b\3\2\2"+
		"\2\22\u008e\3\2\2\2\24\u00a7\3\2\2\2\26\u00a9\3\2\2\2\30\u00c6\3\2\2\2"+
		"\32\u00d2\3\2\2\2\34\u00d5\3\2\2\2\36\u00dc\3\2\2\2 \u00e7\3\2\2\2\"\u00f1"+
		"\3\2\2\2$\u00f3\3\2\2\2&\u00fd\3\2\2\2(\u0107\3\2\2\2*\u0113\3\2\2\2,"+
		"\u0115\3\2\2\2.\u012f\3\2\2\2\60\u0132\3\2\2\2\62\u0140\3\2\2\2\64\u0145"+
		"\3\2\2\2\66\u0149\3\2\2\28\u0162\3\2\2\2:\u0169\3\2\2\2<\u016b\3\2\2\2"+
		">\u0176\3\2\2\2@\u0183\3\2\2\2BD\5\4\3\2CB\3\2\2\2CD\3\2\2\2DF\3\2\2\2"+
		"EG\5\6\4\2FE\3\2\2\2FG\3\2\2\2GK\3\2\2\2HJ\5\b\5\2IH\3\2\2\2JM\3\2\2\2"+
		"KI\3\2\2\2KL\3\2\2\2LQ\3\2\2\2MK\3\2\2\2NP\5\f\7\2ON\3\2\2\2PS\3\2\2\2"+
		"QO\3\2\2\2QR\3\2\2\2RW\3\2\2\2SQ\3\2\2\2TV\5(\25\2UT\3\2\2\2VY\3\2\2\2"+
		"WU\3\2\2\2WX\3\2\2\2X\3\3\2\2\2YW\3\2\2\2Z[\7\27\2\2[\\\7!\2\2\\\5\3\2"+
		"\2\2]^\7\30\2\2^_\7!\2\2_\7\3\2\2\2`a\7\31\2\2ae\7\3\2\2bd\5\n\6\2cb\3"+
		"\2\2\2dg\3\2\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\4\2\2i\t"+
		"\3\2\2\2jk\7\32\2\2kl\7\34\2\2lm\7\3\2\2mn\7\4\2\2n\13\3\2\2\2op\7\5\2"+
		"\2pr\7\3\2\2qs\5\16\b\2rq\3\2\2\2rs\3\2\2\2sw\3\2\2\2tv\5\20\t\2ut\3\2"+
		"\2\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2x}\3\2\2\2yw\3\2\2\2z|\5\22\n\2{z\3"+
		"\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\u0083\3\2\2\2\177}\3\2\2\2\u0080"+
		"\u0082\5\26\f\2\u0081\u0080\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3"+
		"\2\2\2\u0083\u0084\3\2\2\2\u0084\u0086\3\2\2\2\u0085\u0083\3\2\2\2\u0086"+
		"\u0087\7\4\2\2\u0087\r\3\2\2\2\u0088\u0089\7\6\2\2\u0089\u008a\7!\2\2"+
		"\u008a\17\3\2\2\2\u008b\u008c\7\7\2\2\u008c\u008d\7!\2\2\u008d\21\3\2"+
		"\2\2\u008e\u008f\7\b\2\2\u008f\u009a\7\34\2\2\u0090\u0091\7\t\2\2\u0091"+
		"\u0092\7\34\2\2\u0092\u0097\3\2\2\2\u0093\u0094\7\n\2\2\u0094\u0096\7"+
		"\34\2\2\u0095\u0093\3\2\2\2\u0096\u0099\3\2\2\2\u0097\u0095\3\2\2\2\u0097"+
		"\u0098\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u009a\u0090\3\2"+
		"\2\2\u009a\u009b\3\2\2\2\u009b\u009c\3\2\2\2\u009c\u00a0\7\3\2\2\u009d"+
		"\u009f\5\24\13\2\u009e\u009d\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3"+
		"\2\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a3\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3"+
		"\u00a4\7\4\2\2\u00a4\23\3\2\2\2\u00a5\u00a8\5\"\22\2\u00a6\u00a8\5\26"+
		"\f\2\u00a7\u00a5\3\2\2\2\u00a7\u00a6\3\2\2\2\u00a8\25\3\2\2\2\u00a9\u00aa"+
		"\7\32\2\2\u00aa\u00ae\7\34\2\2\u00ab\u00ad\5\30\r\2\u00ac\u00ab\3\2\2"+
		"\2\u00ad\u00b0\3\2\2\2\u00ae\u00ac\3\2\2\2\u00ae\u00af\3\2\2\2\u00af\u00ba"+
		"\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b1\u00b2\7\13\2\2\u00b2\u00b7\5\36\20"+
		"\2\u00b3\u00b4\7\n\2\2\u00b4\u00b6\5\36\20\2\u00b5\u00b3\3\2\2\2\u00b6"+
		"\u00b9\3\2\2\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00bb\3\2"+
		"\2\2\u00b9\u00b7\3\2\2\2\u00ba\u00b1\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb"+
		"\u00c4\3\2\2\2\u00bc\u00c0\7\3\2\2\u00bd\u00bf\5\"\22\2\u00be\u00bd\3"+
		"\2\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00be\3\2\2\2\u00c0\u00c1\3\2\2\2\u00c1"+
		"\u00c3\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3\u00c5\7\4\2\2\u00c4\u00bc\3\2"+
		"\2\2\u00c4\u00c5\3\2\2\2\u00c5\27\3\2\2\2\u00c6\u00c7\7\f\2\2\u00c7\u00c8"+
		"\5\32\16\2\u00c8\u00c9\7\r\2\2\u00c9\31\3\2\2\2\u00ca\u00cf\5\34\17\2"+
		"\u00cb\u00cc\7\n\2\2\u00cc\u00ce\5\34\17\2\u00cd\u00cb\3\2\2\2\u00ce\u00d1"+
		"\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d2\u00ca\3\2\2\2\u00d2\u00d3\3\2\2\2\u00d3\33\3\2\2"+
		"\2\u00d4\u00d6\7\34\2\2\u00d5\u00d4\3\2\2\2\u00d5\u00d6\3\2\2\2\u00d6"+
		"\u00d7\3\2\2\2\u00d7\u00da\7\34\2\2\u00d8\u00d9\7\13\2\2\u00d9\u00db\5"+
		"\36\20\2\u00da\u00d8\3\2\2\2\u00da\u00db\3\2\2\2\u00db\35\3\2\2\2\u00dc"+
		"\u00e1\7\34\2\2\u00dd\u00de\7\16\2\2\u00de\u00df\5 \21\2\u00df\u00e0\7"+
		"\17\2\2\u00e0\u00e2\3\2\2\2\u00e1\u00dd\3\2\2\2\u00e1\u00e2\3\2\2\2\u00e2"+
		"\u00e5\3\2\2\2\u00e3\u00e4\7\20\2\2\u00e4\u00e6\7\21\2\2\u00e5\u00e3\3"+
		"\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\37\3\2\2\2\u00e7\u00ec\5\36\20\2\u00e8"+
		"\u00e9\7\n\2\2\u00e9\u00eb\5\36\20\2\u00ea\u00e8\3\2\2\2\u00eb\u00ee\3"+
		"\2\2\2\u00ec\u00ea\3\2\2\2\u00ec\u00ed\3\2\2\2\u00ed!\3\2\2\2\u00ee\u00ec"+
		"\3\2\2\2\u00ef\u00f2\5$\23\2\u00f0\u00f2\5&\24\2\u00f1\u00ef\3\2\2\2\u00f1"+
		"\u00f0\3\2\2\2\u00f2#\3\2\2\2\u00f3\u00f4\7\22\2\2\u00f4\u00f7\7\34\2"+
		"\2\u00f5\u00f6\7\13\2\2\u00f6\u00f8\5\36\20\2\u00f7\u00f5\3\2\2\2\u00f7"+
		"\u00f8\3\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00fa\7\23\2\2\u00fa\u00fc\5"+
		"\"\22\2\u00fb\u00f9\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc%\3\2\2\2\u00fd\u00fe"+
		"\7\24\2\2\u00fe\u0101\7\34\2\2\u00ff\u0100\7\13\2\2\u0100\u0102\5\36\20"+
		"\2\u0101\u00ff\3\2\2\2\u0101\u0102\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0104"+
		"\7\23\2\2\u0104\u0106\5\"\22\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2"+
		"\u0106\'\3\2\2\2\u0107\u0108\7#\2\2\u0108\u010c\7\3\2\2\u0109\u010b\5"+
		"*\26\2\u010a\u0109\3\2\2\2\u010b\u010e\3\2\2\2\u010c\u010a\3\2\2\2\u010c"+
		"\u010d\3\2\2\2\u010d\u010f\3\2\2\2\u010e\u010c\3\2\2\2\u010f\u0110\7\4"+
		"\2\2\u0110)\3\2\2\2\u0111\u0114\5,\27\2\u0112\u0114\5\66\34\2\u0113\u0111"+
		"\3\2\2\2\u0113\u0112\3\2\2\2\u0114+\3\2\2\2\u0115\u0121\7\35\2\2\u0116"+
		"\u0117\7\f\2\2\u0117\u011c\5\64\33\2\u0118\u0119\7\n\2\2\u0119\u011b\5"+
		"\64\33\2\u011a\u0118\3\2\2\2\u011b\u011e\3\2\2\2\u011c\u011a\3\2\2\2\u011c"+
		"\u011d\3\2\2\2\u011d\u011f\3\2\2\2\u011e\u011c\3\2\2\2\u011f\u0120\7\r"+
		"\2\2\u0120\u0122\3\2\2\2\u0121\u0116\3\2\2\2\u0121\u0122\3\2\2\2\u0122"+
		"\u0123\3\2\2\2\u0123\u0127\7\3\2\2\u0124\u0126\5.\30\2\u0125\u0124\3\2"+
		"\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0128\3\2\2\2\u0128"+
		"\u012a\3\2\2\2\u0129\u0127\3\2\2\2\u012a\u012b\7\4\2\2\u012b-\3\2\2\2"+
		"\u012c\u0130\5\64\33\2\u012d\u0130\5\62\32\2\u012e\u0130\5\60\31\2\u012f"+
		"\u012c\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u012e\3\2\2\2\u0130/\3\2\2\2"+
		"\u0131\u0133\7\35\2\2\u0132\u0131\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134"+
		"\3\2\2\2\u0134\u0135\7\20\2\2\u0135\u013a\5.\30\2\u0136\u0137\7\n\2\2"+
		"\u0137\u0139\5.\30\2\u0138\u0136\3\2\2\2\u0139\u013c\3\2\2\2\u013a\u0138"+
		"\3\2\2\2\u013a\u013b\3\2\2\2\u013b\u013d\3\2\2\2\u013c\u013a\3\2\2\2\u013d"+
		"\u013e\7\21\2\2\u013e\61\3\2\2\2\u013f\u0141\7\35\2\2\u0140\u013f\3\2"+
		"\2\2\u0140\u0141\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\5,\27\2\u0143"+
		"\63\3\2\2\2\u0144\u0146\7\35\2\2\u0145\u0144\3\2\2\2\u0145\u0146\3\2\2"+
		"\2\u0146\u0147\3\2\2\2\u0147\u0148\7!\2\2\u0148\65\3\2\2\2\u0149\u014a"+
		"\7\25\2\2\u014a\u0156\7\35\2\2\u014b\u014c\7\f\2\2\u014c\u0151\58\35\2"+
		"\u014d\u014e\7\n\2\2\u014e\u0150\58\35\2\u014f\u014d\3\2\2\2\u0150\u0153"+
		"\3\2\2\2\u0151\u014f\3\2\2\2\u0151\u0152\3\2\2\2\u0152\u0154\3\2\2\2\u0153"+
		"\u0151\3\2\2\2\u0154\u0155\7\r\2\2\u0155\u0157\3\2\2\2\u0156\u014b\3\2"+
		"\2\2\u0156\u0157\3\2\2\2\u0157\u0158\3\2\2\2\u0158\u015c\7\3\2\2\u0159"+
		"\u015b\58\35\2\u015a\u0159\3\2\2\2\u015b\u015e\3\2\2\2\u015c\u015a\3\2"+
		"\2\2\u015c\u015d\3\2\2\2\u015d\u015f\3\2\2\2\u015e\u015c\3\2\2\2\u015f"+
		"\u0160\7\4\2\2\u0160\67\3\2\2\2\u0161\u0163\7\35\2\2\u0162\u0161\3\2\2"+
		"\2\u0162\u0163\3\2\2\2\u0163\u0164\3\2\2\2\u0164\u0165\5:\36\2\u01659"+
		"\3\2\2\2\u0166\u016a\5<\37\2\u0167\u016a\5> \2\u0168\u016a\5@!\2\u0169"+
		"\u0166\3\2\2\2\u0169\u0167\3\2\2\2\u0169\u0168\3\2\2\2\u016a;\3\2\2\2"+
		"\u016b\u0170\7\35\2\2\u016c\u016d\7\16\2\2\u016d\u016e\5 \21\2\u016e\u016f"+
		"\7\17\2\2\u016f\u0171\3\2\2\2\u0170\u016c\3\2\2\2\u0170\u0171\3\2\2\2"+
		"\u0171\u0174\3\2\2\2\u0172\u0173\7\20\2\2\u0173\u0175\7\21\2\2\u0174\u0172"+
		"\3\2\2\2\u0174\u0175\3\2\2\2\u0175=\3\2\2\2\u0176\u0177\7\f\2\2\u0177"+
		"\u0181\7\r\2\2\u0178\u0179\7\26\2\2\u0179\u017e\5<\37\2\u017a\u017b\7"+
		"\n\2\2\u017b\u017d\5<\37\2\u017c\u017a\3\2\2\2\u017d\u0180\3\2\2\2\u017e"+
		"\u017c\3\2\2\2\u017e\u017f\3\2\2\2\u017f\u0182\3\2\2\2\u0180\u017e\3\2"+
		"\2\2\u0181\u0178\3\2\2\2\u0181\u0182\3\2\2\2\u0182?\3\2\2\2\u0183\u0184"+
		"\7\26\2\2\u0184\u0185\7\35\2\2\u0185A\3\2\2\2\64CFKQWerw}\u0083\u0097"+
		"\u009a\u00a0\u00a7\u00ae\u00b7\u00ba\u00c0\u00c4\u00cf\u00d2\u00d5\u00da"+
		"\u00e1\u00e5\u00ec\u00f1\u00f7\u00fb\u0101\u0105\u010c\u0113\u011c\u0121"+
		"\u0127\u012f\u0132\u013a\u0140\u0145\u0151\u0156\u015c\u0162\u0169\u0170"+
		"\u0174\u017e\u0181";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}