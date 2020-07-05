// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLang.g4 by ANTLR 4.8
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
		T__17=18, LANG=19, FILE=20, HELPER=21, FUNC=22, TEXT=23, ANY_ID=24, ID=25, 
		ID_RPL=26, WS=27, ESCAPED_QUOTE=28, STRING=29, NUMBER=30, MODEL=31;
	public static final int
		RULE_domainModel = 0, RULE_lang = 1, RULE_file = 2, RULE_helperBlock = 3, 
		RULE_helperFunc = 4, RULE_tmplBlock = 5, RULE_tmplPkg = 6, RULE_tmplUse = 7, 
		RULE_tmplImpl = 8, RULE_tmplImplContent = 9, RULE_tmplFunc = 10, RULE_tmplCurrying = 11, 
		RULE_tmplCurryingParam = 12, RULE_tmplParam = 13, RULE_tmplType = 14, 
		RULE_tmplGeneric = 15, RULE_tmplExpression = 16, RULE_tmplVal = 17, RULE_tmplVar = 18, 
		RULE_modelBlock = 19, RULE_modelNewEntity = 20, RULE_modelValueType = 21, 
		RULE_modelTbl = 22, RULE_modelEntityAsAttribut = 23, RULE_modelAttribut = 24;
	private static String[] makeRuleNames() {
		return new String[] {
			"domainModel", "lang", "file", "helperBlock", "helperFunc", "tmplBlock", 
			"tmplPkg", "tmplUse", "tmplImpl", "tmplImplContent", "tmplFunc", "tmplCurrying", 
			"tmplCurryingParam", "tmplParam", "tmplType", "tmplGeneric", "tmplExpression", 
			"tmplVal", "tmplVar", "modelBlock", "modelNewEntity", "modelValueType", 
			"modelTbl", "modelEntityAsAttribut", "modelAttribut"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'{'", "'}'", "'tmpl'", "'pkg'", "'use'", "'impl'", "'for'", "','", 
			"':'", "'('", "')'", "'<'", "'>'", "'['", "']'", "'val'", "'='", "'var'", 
			"'lang'", "'file'", "'helper'", "'func'", null, null, null, null, null, 
			"'\\\"'", null, null, "'model'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, "LANG", "FILE", "HELPER", "FUNC", 
			"TEXT", "ANY_ID", "ID", "ID_RPL", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER", 
			"MODEL"
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
			setState(51);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LANG) {
				{
				setState(50);
				lang();
				}
			}

			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FILE) {
				{
				setState(53);
				file();
				}
			}

			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HELPER) {
				{
				{
				setState(56);
				((DomainModelContext)_localctx).helperBlock = helperBlock();
				((DomainModelContext)_localctx).helperBlocks.add(((DomainModelContext)_localctx).helperBlock);
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(62);
				((DomainModelContext)_localctx).tmplBlock = tmplBlock();
				((DomainModelContext)_localctx).tmplBlocks.add(((DomainModelContext)_localctx).tmplBlock);
				}
				}
				setState(67);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==MODEL) {
				{
				{
				setState(68);
				((DomainModelContext)_localctx).modelBlock = modelBlock();
				((DomainModelContext)_localctx).modelBlocks.add(((DomainModelContext)_localctx).modelBlock);
				}
				}
				setState(73);
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
			setState(74);
			match(LANG);
			setState(75);
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
			setState(77);
			match(FILE);
			setState(78);
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
			setState(80);
			match(HELPER);
			setState(81);
			match(T__0);
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FUNC) {
				{
				{
				setState(82);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
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
			setState(90);
			match(FUNC);
			setState(91);
			((HelperFuncContext)_localctx).name = match(ANY_ID);
			setState(92);
			match(T__0);
			setState(93);
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
			setState(95);
			match(T__2);
			setState(96);
			match(T__0);
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(97);
				((TmplBlockContext)_localctx).tmplPakage = tmplPkg();
				}
			}

			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(100);
				((TmplBlockContext)_localctx).tmplUse = tmplUse();
				((TmplBlockContext)_localctx).tmplUses.add(((TmplBlockContext)_localctx).tmplUse);
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(106);
				((TmplBlockContext)_localctx).tmplImpl = tmplImpl();
				((TmplBlockContext)_localctx).tmplImpls.add(((TmplBlockContext)_localctx).tmplImpl);
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==FUNC) {
				{
				{
				setState(112);
				((TmplBlockContext)_localctx).tmplFunc = tmplFunc();
				((TmplBlockContext)_localctx).tmplFuncs.add(((TmplBlockContext)_localctx).tmplFunc);
				}
				}
				setState(117);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(118);
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
			setState(120);
			match(T__3);
			setState(121);
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
			setState(123);
			match(T__4);
			setState(124);
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
			setState(126);
			match(T__5);
			setState(127);
			((TmplImplContext)_localctx).name = match(ANY_ID);
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				{
				setState(128);
				match(T__6);
				setState(129);
				((TmplImplContext)_localctx).forName = match(ANY_ID);
				}
				setState(135);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(131);
					match(T__7);
					setState(132);
					((TmplImplContext)_localctx).ANY_ID = match(ANY_ID);
					((TmplImplContext)_localctx).forNames.add(((TmplImplContext)_localctx).ANY_ID);
					}
					}
					setState(137);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(140);
			match(T__0);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__15) | (1L << T__17) | (1L << FUNC))) != 0)) {
				{
				{
				setState(141);
				((TmplImplContext)_localctx).tmplImplContent = tmplImplContent();
				((TmplImplContext)_localctx).tmplImplContents.add(((TmplImplContext)_localctx).tmplImplContent);
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(147);
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
			setState(151);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(149);
				tmplExpression();
				}
				break;
			case FUNC:
				enterOuterAlt(_localctx, 2);
				{
				setState(150);
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
			setState(153);
			match(FUNC);
			setState(154);
			((TmplFuncContext)_localctx).name = match(ANY_ID);
			setState(158);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(155);
				((TmplFuncContext)_localctx).tmplCurrying = tmplCurrying();
				((TmplFuncContext)_localctx).curries.add(((TmplFuncContext)_localctx).tmplCurrying);
				}
				}
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(161);
				match(T__8);
				setState(162);
				((TmplFuncContext)_localctx).tmplType = tmplType();
				((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(163);
					match(T__7);
					setState(164);
					((TmplFuncContext)_localctx).tmplType = tmplType();
					((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
					}
					}
					setState(169);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(172);
				match(T__0);
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__15 || _la==T__17) {
					{
					{
					setState(173);
					((TmplFuncContext)_localctx).tmplExpression = tmplExpression();
					((TmplFuncContext)_localctx).exprs.add(((TmplFuncContext)_localctx).tmplExpression);
					}
					}
					setState(178);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(179);
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
			setState(182);
			match(T__9);
			setState(183);
			((TmplCurryingContext)_localctx).param = tmplCurryingParam();
			setState(184);
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
			setState(194);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ANY_ID) {
				{
				{
				setState(186);
				((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
				((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(187);
					match(T__7);
					setState(188);
					((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
					((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
					}
					}
					setState(193);
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
			setState(197);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(196);
				((TmplParamContext)_localctx).accessor = match(ANY_ID);
				}
				break;
			}
			setState(199);
			((TmplParamContext)_localctx).name = match(ANY_ID);
			setState(202);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(200);
				match(T__8);
				setState(201);
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
			setState(204);
			((TmplTypeContext)_localctx).type = match(ANY_ID);
			setState(209);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(205);
				match(T__11);
				{
				setState(206);
				((TmplTypeContext)_localctx).generic = tmplGeneric();
				}
				setState(207);
				match(T__12);
				}
			}

			setState(213);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(211);
				((TmplTypeContext)_localctx).array = match(T__13);
				setState(212);
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
			setState(215);
			((TmplGenericContext)_localctx).tmplType = tmplType();
			((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(216);
				match(T__7);
				setState(217);
				((TmplGenericContext)_localctx).tmplType = tmplType();
				((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
				}
				}
				setState(222);
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
			setState(225);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
				enterOuterAlt(_localctx, 1);
				{
				setState(223);
				tmplVal();
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 2);
				{
				setState(224);
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
			setState(227);
			match(T__15);
			setState(228);
			((TmplValContext)_localctx).name = match(ANY_ID);
			setState(231);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(229);
				match(T__8);
				setState(230);
				((TmplValContext)_localctx).type = tmplType();
				}
			}

			setState(235);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(233);
				match(T__16);
				setState(234);
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
			setState(237);
			match(T__17);
			setState(238);
			((TmplVarContext)_localctx).name = match(ANY_ID);
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__8) {
				{
				setState(239);
				match(T__8);
				setState(240);
				((TmplVarContext)_localctx).type = tmplType();
				}
			}

			setState(245);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__16) {
				{
				setState(243);
				match(T__16);
				setState(244);
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
		public ModelNewEntityContext modelNewEntity;
		public List<ModelNewEntityContext> modelEntities = new ArrayList<ModelNewEntityContext>();
		public TerminalNode MODEL() { return getToken(TLangParser.MODEL, 0); }
		public List<ModelNewEntityContext> modelNewEntity() {
			return getRuleContexts(ModelNewEntityContext.class);
		}
		public ModelNewEntityContext modelNewEntity(int i) {
			return getRuleContext(ModelNewEntityContext.class,i);
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
			setState(247);
			match(MODEL);
			setState(248);
			match(T__0);
			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(249);
				((ModelBlockContext)_localctx).modelNewEntity = modelNewEntity();
				((ModelBlockContext)_localctx).modelEntities.add(((ModelBlockContext)_localctx).modelNewEntity);
				}
				}
				setState(254);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(255);
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
		enterRule(_localctx, 40, RULE_modelNewEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			((ModelNewEntityContext)_localctx).type = match(ID);
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__9) {
				{
				setState(258);
				match(T__9);
				{
				{
				setState(259);
				((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
				((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
				}
				setState(264);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(260);
					match(T__7);
					setState(261);
					((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
					((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
					}
					}
					setState(266);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(267);
				match(T__10);
				}
			}

			setState(271);
			match(T__0);
			setState(275);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << ID) | (1L << STRING))) != 0)) {
				{
				{
				setState(272);
				((ModelNewEntityContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityContext)_localctx).decl.add(((ModelNewEntityContext)_localctx).modelValueType);
				}
				}
				setState(277);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(278);
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
		enterRule(_localctx, 42, RULE_modelValueType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				{
				setState(280);
				modelAttribut();
				}
				break;
			case 2:
				{
				setState(281);
				modelEntityAsAttribut();
				}
				break;
			case 3:
				{
				setState(282);
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
		enterRule(_localctx, 44, RULE_modelTbl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(285);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(288);
			match(T__13);
			}
			{
			{
			setState(289);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(294);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(290);
				match(T__7);
				setState(291);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(296);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(297);
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
		enterRule(_localctx, 46, RULE_modelEntityAsAttribut);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,38,_ctx) ) {
			case 1:
				{
				setState(299);
				((ModelEntityAsAttributContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(302);
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
		enterRule(_localctx, 48, RULE_modelAttribut);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(305);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(304);
				((ModelAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(307);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!\u0138\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\3\2\5\2\66\n\2\3\2\5\29\n\2\3\2\7\2<\n\2\f\2\16\2?\13\2\3\2"+
		"\7\2B\n\2\f\2\16\2E\13\2\3\2\7\2H\n\2\f\2\16\2K\13\2\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\7\5V\n\5\f\5\16\5Y\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\7\3\7\3\7\5\7e\n\7\3\7\7\7h\n\7\f\7\16\7k\13\7\3\7\7\7n\n\7\f\7\16"+
		"\7q\13\7\3\7\7\7t\n\7\f\7\16\7w\13\7\3\7\3\7\3\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13\n\5\n\u008d"+
		"\n\n\3\n\3\n\7\n\u0091\n\n\f\n\16\n\u0094\13\n\3\n\3\n\3\13\3\13\5\13"+
		"\u009a\n\13\3\f\3\f\3\f\7\f\u009f\n\f\f\f\16\f\u00a2\13\f\3\f\3\f\3\f"+
		"\3\f\7\f\u00a8\n\f\f\f\16\f\u00ab\13\f\5\f\u00ad\n\f\3\f\3\f\7\f\u00b1"+
		"\n\f\f\f\16\f\u00b4\13\f\3\f\5\f\u00b7\n\f\3\r\3\r\3\r\3\r\3\16\3\16\3"+
		"\16\7\16\u00c0\n\16\f\16\16\16\u00c3\13\16\5\16\u00c5\n\16\3\17\5\17\u00c8"+
		"\n\17\3\17\3\17\3\17\5\17\u00cd\n\17\3\20\3\20\3\20\3\20\3\20\5\20\u00d4"+
		"\n\20\3\20\3\20\5\20\u00d8\n\20\3\21\3\21\3\21\7\21\u00dd\n\21\f\21\16"+
		"\21\u00e0\13\21\3\22\3\22\5\22\u00e4\n\22\3\23\3\23\3\23\3\23\5\23\u00ea"+
		"\n\23\3\23\3\23\5\23\u00ee\n\23\3\24\3\24\3\24\3\24\5\24\u00f4\n\24\3"+
		"\24\3\24\5\24\u00f8\n\24\3\25\3\25\3\25\7\25\u00fd\n\25\f\25\16\25\u0100"+
		"\13\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\7\26\u0109\n\26\f\26\16\26\u010c"+
		"\13\26\3\26\3\26\5\26\u0110\n\26\3\26\3\26\7\26\u0114\n\26\f\26\16\26"+
		"\u0117\13\26\3\26\3\26\3\27\3\27\3\27\5\27\u011e\n\27\3\30\5\30\u0121"+
		"\n\30\3\30\3\30\3\30\3\30\7\30\u0127\n\30\f\30\16\30\u012a\13\30\3\30"+
		"\3\30\3\31\5\31\u012f\n\31\3\31\3\31\3\32\5\32\u0134\n\32\3\32\3\32\3"+
		"\32\2\2\33\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\2\2\2"+
		"\u0147\2\65\3\2\2\2\4L\3\2\2\2\6O\3\2\2\2\bR\3\2\2\2\n\\\3\2\2\2\fa\3"+
		"\2\2\2\16z\3\2\2\2\20}\3\2\2\2\22\u0080\3\2\2\2\24\u0099\3\2\2\2\26\u009b"+
		"\3\2\2\2\30\u00b8\3\2\2\2\32\u00c4\3\2\2\2\34\u00c7\3\2\2\2\36\u00ce\3"+
		"\2\2\2 \u00d9\3\2\2\2\"\u00e3\3\2\2\2$\u00e5\3\2\2\2&\u00ef\3\2\2\2(\u00f9"+
		"\3\2\2\2*\u0103\3\2\2\2,\u011d\3\2\2\2.\u0120\3\2\2\2\60\u012e\3\2\2\2"+
		"\62\u0133\3\2\2\2\64\66\5\4\3\2\65\64\3\2\2\2\65\66\3\2\2\2\668\3\2\2"+
		"\2\679\5\6\4\28\67\3\2\2\289\3\2\2\29=\3\2\2\2:<\5\b\5\2;:\3\2\2\2<?\3"+
		"\2\2\2=;\3\2\2\2=>\3\2\2\2>C\3\2\2\2?=\3\2\2\2@B\5\f\7\2A@\3\2\2\2BE\3"+
		"\2\2\2CA\3\2\2\2CD\3\2\2\2DI\3\2\2\2EC\3\2\2\2FH\5(\25\2GF\3\2\2\2HK\3"+
		"\2\2\2IG\3\2\2\2IJ\3\2\2\2J\3\3\2\2\2KI\3\2\2\2LM\7\25\2\2MN\7\37\2\2"+
		"N\5\3\2\2\2OP\7\26\2\2PQ\7\37\2\2Q\7\3\2\2\2RS\7\27\2\2SW\7\3\2\2TV\5"+
		"\n\6\2UT\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2\2\2YW\3\2\2\2Z[\7"+
		"\4\2\2[\t\3\2\2\2\\]\7\30\2\2]^\7\32\2\2^_\7\3\2\2_`\7\4\2\2`\13\3\2\2"+
		"\2ab\7\5\2\2bd\7\3\2\2ce\5\16\b\2dc\3\2\2\2de\3\2\2\2ei\3\2\2\2fh\5\20"+
		"\t\2gf\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2jo\3\2\2\2ki\3\2\2\2ln\5\22"+
		"\n\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2pu\3\2\2\2qo\3\2\2\2rt\5\26"+
		"\f\2sr\3\2\2\2tw\3\2\2\2us\3\2\2\2uv\3\2\2\2vx\3\2\2\2wu\3\2\2\2xy\7\4"+
		"\2\2y\r\3\2\2\2z{\7\6\2\2{|\7\37\2\2|\17\3\2\2\2}~\7\7\2\2~\177\7\37\2"+
		"\2\177\21\3\2\2\2\u0080\u0081\7\b\2\2\u0081\u008c\7\32\2\2\u0082\u0083"+
		"\7\t\2\2\u0083\u0084\7\32\2\2\u0084\u0089\3\2\2\2\u0085\u0086\7\n\2\2"+
		"\u0086\u0088\7\32\2\2\u0087\u0085\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087"+
		"\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u0089\3\2\2\2\u008c"+
		"\u0082\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u0092\7\3"+
		"\2\2\u008f\u0091\5\24\13\2\u0090\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092"+
		"\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0095\3\2\2\2\u0094\u0092\3\2"+
		"\2\2\u0095\u0096\7\4\2\2\u0096\23\3\2\2\2\u0097\u009a\5\"\22\2\u0098\u009a"+
		"\5\26\f\2\u0099\u0097\3\2\2\2\u0099\u0098\3\2\2\2\u009a\25\3\2\2\2\u009b"+
		"\u009c\7\30\2\2\u009c\u00a0\7\32\2\2\u009d\u009f\5\30\r\2\u009e\u009d"+
		"\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0\u009e\3\2\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u00ac\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a3\u00a4\7\13\2\2\u00a4\u00a9\5"+
		"\36\20\2\u00a5\u00a6\7\n\2\2\u00a6\u00a8\5\36\20\2\u00a7\u00a5\3\2\2\2"+
		"\u00a8\u00ab\3\2\2\2\u00a9\u00a7\3\2\2\2\u00a9\u00aa\3\2\2\2\u00aa\u00ad"+
		"\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\u00a3\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad"+
		"\u00b6\3\2\2\2\u00ae\u00b2\7\3\2\2\u00af\u00b1\5\"\22\2\u00b0\u00af\3"+
		"\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b2\u00b3\3\2\2\2\u00b3"+
		"\u00b5\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5\u00b7\7\4\2\2\u00b6\u00ae\3\2"+
		"\2\2\u00b6\u00b7\3\2\2\2\u00b7\27\3\2\2\2\u00b8\u00b9\7\f\2\2\u00b9\u00ba"+
		"\5\32\16\2\u00ba\u00bb\7\r\2\2\u00bb\31\3\2\2\2\u00bc\u00c1\5\34\17\2"+
		"\u00bd\u00be\7\n\2\2\u00be\u00c0\5\34\17\2\u00bf\u00bd\3\2\2\2\u00c0\u00c3"+
		"\3\2\2\2\u00c1\u00bf\3\2\2\2\u00c1\u00c2\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3"+
		"\u00c1\3\2\2\2\u00c4\u00bc\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\33\3\2\2"+
		"\2\u00c6\u00c8\7\32\2\2\u00c7\u00c6\3\2\2\2\u00c7\u00c8\3\2\2\2\u00c8"+
		"\u00c9\3\2\2\2\u00c9\u00cc\7\32\2\2\u00ca\u00cb\7\13\2\2\u00cb\u00cd\5"+
		"\36\20\2\u00cc\u00ca\3\2\2\2\u00cc\u00cd\3\2\2\2\u00cd\35\3\2\2\2\u00ce"+
		"\u00d3\7\32\2\2\u00cf\u00d0\7\16\2\2\u00d0\u00d1\5 \21\2\u00d1\u00d2\7"+
		"\17\2\2\u00d2\u00d4\3\2\2\2\u00d3\u00cf\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"\u00d7\3\2\2\2\u00d5\u00d6\7\20\2\2\u00d6\u00d8\7\21\2\2\u00d7\u00d5\3"+
		"\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\37\3\2\2\2\u00d9\u00de\5\36\20\2\u00da"+
		"\u00db\7\n\2\2\u00db\u00dd\5\36\20\2\u00dc\u00da\3\2\2\2\u00dd\u00e0\3"+
		"\2\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df!\3\2\2\2\u00e0\u00de"+
		"\3\2\2\2\u00e1\u00e4\5$\23\2\u00e2\u00e4\5&\24\2\u00e3\u00e1\3\2\2\2\u00e3"+
		"\u00e2\3\2\2\2\u00e4#\3\2\2\2\u00e5\u00e6\7\22\2\2\u00e6\u00e9\7\32\2"+
		"\2\u00e7\u00e8\7\13\2\2\u00e8\u00ea\5\36\20\2\u00e9\u00e7\3\2\2\2\u00e9"+
		"\u00ea\3\2\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00ec\7\23\2\2\u00ec\u00ee\5"+
		"\"\22\2\u00ed\u00eb\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee%\3\2\2\2\u00ef\u00f0"+
		"\7\24\2\2\u00f0\u00f3\7\32\2\2\u00f1\u00f2\7\13\2\2\u00f2\u00f4\5\36\20"+
		"\2\u00f3\u00f1\3\2\2\2\u00f3\u00f4\3\2\2\2\u00f4\u00f7\3\2\2\2\u00f5\u00f6"+
		"\7\23\2\2\u00f6\u00f8\5\"\22\2\u00f7\u00f5\3\2\2\2\u00f7\u00f8\3\2\2\2"+
		"\u00f8\'\3\2\2\2\u00f9\u00fa\7!\2\2\u00fa\u00fe\7\3\2\2\u00fb\u00fd\5"+
		"*\26\2\u00fc\u00fb\3\2\2\2\u00fd\u0100\3\2\2\2\u00fe\u00fc\3\2\2\2\u00fe"+
		"\u00ff\3\2\2\2\u00ff\u0101\3\2\2\2\u0100\u00fe\3\2\2\2\u0101\u0102\7\4"+
		"\2\2\u0102)\3\2\2\2\u0103\u010f\7\33\2\2\u0104\u0105\7\f\2\2\u0105\u010a"+
		"\5\62\32\2\u0106\u0107\7\n\2\2\u0107\u0109\5\62\32\2\u0108\u0106\3\2\2"+
		"\2\u0109\u010c\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u010b\3\2\2\2\u010b\u010d"+
		"\3\2\2\2\u010c\u010a\3\2\2\2\u010d\u010e\7\r\2\2\u010e\u0110\3\2\2\2\u010f"+
		"\u0104\3\2\2\2\u010f\u0110\3\2\2\2\u0110\u0111\3\2\2\2\u0111\u0115\7\3"+
		"\2\2\u0112\u0114\5,\27\2\u0113\u0112\3\2\2\2\u0114\u0117\3\2\2\2\u0115"+
		"\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116\u0118\3\2\2\2\u0117\u0115\3\2"+
		"\2\2\u0118\u0119\7\4\2\2\u0119+\3\2\2\2\u011a\u011e\5\62\32\2\u011b\u011e"+
		"\5\60\31\2\u011c\u011e\5.\30\2\u011d\u011a\3\2\2\2\u011d\u011b\3\2\2\2"+
		"\u011d\u011c\3\2\2\2\u011e-\3\2\2\2\u011f\u0121\7\33\2\2\u0120\u011f\3"+
		"\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0123\7\20\2\2\u0123"+
		"\u0128\5,\27\2\u0124\u0125\7\n\2\2\u0125\u0127\5,\27\2\u0126\u0124\3\2"+
		"\2\2\u0127\u012a\3\2\2\2\u0128\u0126\3\2\2\2\u0128\u0129\3\2\2\2\u0129"+
		"\u012b\3\2\2\2\u012a\u0128\3\2\2\2\u012b\u012c\7\21\2\2\u012c/\3\2\2\2"+
		"\u012d\u012f\7\33\2\2\u012e\u012d\3\2\2\2\u012e\u012f\3\2\2\2\u012f\u0130"+
		"\3\2\2\2\u0130\u0131\5*\26\2\u0131\61\3\2\2\2\u0132\u0134\7\33\2\2\u0133"+
		"\u0132\3\2\2\2\u0133\u0134\3\2\2\2\u0134\u0135\3\2\2\2\u0135\u0136\7\37"+
		"\2\2\u0136\63\3\2\2\2*\658=CIWdiou\u0089\u008c\u0092\u0099\u00a0\u00a9"+
		"\u00ac\u00b2\u00b6\u00c1\u00c4\u00c7\u00cc\u00d3\u00d7\u00de\u00e3\u00e9"+
		"\u00ed\u00f3\u00f7\u00fe\u010a\u010f\u0115\u011d\u0120\u0128\u012e\u0133";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}