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
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, TEXT=27, ID=28, WS=29, STRING=30, NUMBER=31;
	public static final int
		RULE_domainModel = 0, RULE_lang = 1, RULE_file = 2, RULE_helperBlock = 3, 
		RULE_helperFunc = 4, RULE_tmplBlock = 5, RULE_tmplPkg = 6, RULE_tmplUse = 7, 
		RULE_tmplImpl = 8, RULE_tmplImplContent = 9, RULE_tmplFunc = 10, RULE_tmplCurrying = 11, 
		RULE_tmplCurryingParam = 12, RULE_tmplParam = 13, RULE_tmplType = 14, 
		RULE_tmplGeneric = 15, RULE_tmplExpression = 16, RULE_tmplVal = 17, RULE_tmplVar = 18, 
		RULE_modelBlock = 19, RULE_modelContent = 20, RULE_modelNewEntity = 21, 
		RULE_modelValueType = 22, RULE_modelTbl = 23, RULE_modelEntityAsAttribut = 24, 
		RULE_modelAttribut = 25, RULE_modelSetEntity = 26, RULE_modelSetAttribute = 27, 
		RULE_modelSetValueType = 28, RULE_modelSetType = 29, RULE_modelGeneric = 30, 
		RULE_modelSetFuncDef = 31, RULE_modelSetRef = 32;
	private static String[] makeRuleNames() {
		return new String[] {
			"domainModel", "lang", "file", "helperBlock", "helperFunc", "tmplBlock", 
			"tmplPkg", "tmplUse", "tmplImpl", "tmplImplContent", "tmplFunc", "tmplCurrying", 
			"tmplCurryingParam", "tmplParam", "tmplType", "tmplGeneric", "tmplExpression", 
			"tmplVal", "tmplVar", "modelBlock", "modelContent", "modelNewEntity", 
			"modelValueType", "modelTbl", "modelEntityAsAttribut", "modelAttribut", 
			"modelSetEntity", "modelSetAttribute", "modelSetValueType", "modelSetType", 
			"modelGeneric", "modelSetFuncDef", "modelSetRef"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'lang'", "'file'", "'helper'", "'{'", "'}'", "'func'", "'tmpl'", 
			"'pkg'", "'use'", "'impl'", "'for'", "','", "':'", "'('", "')'", "'<'", 
			"'>'", "'['", "']'", "'val'", "'='", "'var'", "'model'", "'let'", "'set'", 
			"'->'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, "TEXT", "ID", "WS", "STRING", "NUMBER"
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
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(66);
				lang();
				}
			}

			setState(70);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(69);
				file();
				}
			}

			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__2) {
				{
				{
				setState(72);
				((DomainModelContext)_localctx).helperBlock = helperBlock();
				((DomainModelContext)_localctx).helperBlocks.add(((DomainModelContext)_localctx).helperBlock);
				}
				}
				setState(77);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(78);
				((DomainModelContext)_localctx).tmplBlock = tmplBlock();
				((DomainModelContext)_localctx).tmplBlocks.add(((DomainModelContext)_localctx).tmplBlock);
				}
				}
				setState(83);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__22) {
				{
				{
				setState(84);
				((DomainModelContext)_localctx).modelBlock = modelBlock();
				((DomainModelContext)_localctx).modelBlocks.add(((DomainModelContext)_localctx).modelBlock);
				}
				}
				setState(89);
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
			setState(90);
			match(T__0);
			setState(91);
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
			setState(93);
			match(T__1);
			setState(94);
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
			setState(96);
			match(T__2);
			setState(97);
			match(T__3);
			setState(101);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(98);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(104);
			match(T__4);
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
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
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
			setState(106);
			match(T__5);
			setState(107);
			((HelperFuncContext)_localctx).name = match(ID);
			setState(108);
			match(T__3);
			setState(109);
			match(T__4);
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
			setState(111);
			match(T__6);
			setState(112);
			match(T__3);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(113);
				((TmplBlockContext)_localctx).tmplPakage = tmplPkg();
				}
			}

			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(116);
				((TmplBlockContext)_localctx).tmplUse = tmplUse();
				((TmplBlockContext)_localctx).tmplUses.add(((TmplBlockContext)_localctx).tmplUse);
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(122);
				((TmplBlockContext)_localctx).tmplImpl = tmplImpl();
				((TmplBlockContext)_localctx).tmplImpls.add(((TmplBlockContext)_localctx).tmplImpl);
				}
				}
				setState(127);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(131);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(128);
				((TmplBlockContext)_localctx).tmplFunc = tmplFunc();
				((TmplBlockContext)_localctx).tmplFuncs.add(((TmplBlockContext)_localctx).tmplFunc);
				}
				}
				setState(133);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(134);
			match(T__4);
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
			setState(136);
			match(T__7);
			setState(137);
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
			setState(139);
			match(T__8);
			setState(140);
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
		public Token ID;
		public List<Token> forNames = new ArrayList<Token>();
		public TmplImplContentContext tmplImplContent;
		public List<TmplImplContentContext> tmplImplContents = new ArrayList<TmplImplContentContext>();
		public List<TerminalNode> ID() { return getTokens(TLangParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangParser.ID, i);
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
			setState(142);
			match(T__9);
			setState(143);
			((TmplImplContext)_localctx).name = match(ID);
			setState(154);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				{
				setState(144);
				match(T__10);
				setState(145);
				((TmplImplContext)_localctx).forName = match(ID);
				}
				setState(151);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(147);
					match(T__11);
					setState(148);
					((TmplImplContext)_localctx).ID = match(ID);
					((TmplImplContext)_localctx).forNames.add(((TmplImplContext)_localctx).ID);
					}
					}
					setState(153);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(156);
			match(T__3);
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__5) | (1L << T__19) | (1L << T__21))) != 0)) {
				{
				{
				setState(157);
				((TmplImplContext)_localctx).tmplImplContent = tmplImplContent();
				((TmplImplContext)_localctx).tmplImplContents.add(((TmplImplContext)_localctx).tmplImplContent);
				}
				}
				setState(162);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(163);
			match(T__4);
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
			setState(167);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
			case T__21:
				enterOuterAlt(_localctx, 1);
				{
				setState(165);
				tmplExpression();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(166);
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
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
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
			setState(169);
			match(T__5);
			setState(170);
			((TmplFuncContext)_localctx).name = match(ID);
			setState(174);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__13) {
				{
				{
				setState(171);
				((TmplFuncContext)_localctx).tmplCurrying = tmplCurrying();
				((TmplFuncContext)_localctx).curries.add(((TmplFuncContext)_localctx).tmplCurrying);
				}
				}
				setState(176);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(177);
				match(T__12);
				setState(178);
				((TmplFuncContext)_localctx).tmplType = tmplType();
				((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(179);
					match(T__11);
					setState(180);
					((TmplFuncContext)_localctx).tmplType = tmplType();
					((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
					}
					}
					setState(185);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(196);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(188);
				match(T__3);
				setState(192);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__19 || _la==T__21) {
					{
					{
					setState(189);
					((TmplFuncContext)_localctx).tmplExpression = tmplExpression();
					((TmplFuncContext)_localctx).exprs.add(((TmplFuncContext)_localctx).tmplExpression);
					}
					}
					setState(194);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(195);
				match(T__4);
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
			setState(198);
			match(T__13);
			setState(199);
			((TmplCurryingContext)_localctx).param = tmplCurryingParam();
			setState(200);
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
			setState(210);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				{
				setState(202);
				((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
				((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
				}
				setState(207);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(203);
					match(T__11);
					setState(204);
					((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
					((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
					}
					}
					setState(209);
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
		public List<TerminalNode> ID() { return getTokens(TLangParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangParser.ID, i);
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
			setState(213);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				{
				setState(212);
				((TmplParamContext)_localctx).accessor = match(ID);
				}
				break;
			}
			setState(215);
			((TmplParamContext)_localctx).name = match(ID);
			setState(218);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(216);
				match(T__12);
				setState(217);
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
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
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
			setState(220);
			((TmplTypeContext)_localctx).type = match(ID);
			setState(225);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(221);
				match(T__15);
				{
				setState(222);
				((TmplTypeContext)_localctx).generic = tmplGeneric();
				}
				setState(223);
				match(T__16);
				}
			}

			setState(229);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(227);
				((TmplTypeContext)_localctx).array = match(T__17);
				setState(228);
				match(T__18);
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
			setState(231);
			((TmplGenericContext)_localctx).tmplType = tmplType();
			((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(232);
				match(T__11);
				setState(233);
				((TmplGenericContext)_localctx).tmplType = tmplType();
				((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
				}
				}
				setState(238);
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
			setState(241);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(239);
				tmplVal();
				}
				break;
			case T__21:
				enterOuterAlt(_localctx, 2);
				{
				setState(240);
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
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
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
			setState(243);
			match(T__19);
			setState(244);
			((TmplValContext)_localctx).name = match(ID);
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(245);
				match(T__12);
				setState(246);
				((TmplValContext)_localctx).type = tmplType();
				}
			}

			setState(251);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(249);
				match(T__20);
				setState(250);
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
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
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
			setState(253);
			match(T__21);
			setState(254);
			((TmplVarContext)_localctx).name = match(ID);
			setState(257);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__12) {
				{
				setState(255);
				match(T__12);
				setState(256);
				((TmplVarContext)_localctx).type = tmplType();
				}
			}

			setState(261);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__20) {
				{
				setState(259);
				match(T__20);
				setState(260);
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
			setState(263);
			match(T__22);
			setState(264);
			match(T__3);
			setState(268);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__23 || _la==T__24) {
				{
				{
				setState(265);
				((ModelBlockContext)_localctx).modelContent = modelContent();
				((ModelBlockContext)_localctx).modelContents.add(((ModelBlockContext)_localctx).modelContent);
				}
				}
				setState(270);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(271);
			match(T__4);
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
			setState(275);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__23:
				enterOuterAlt(_localctx, 1);
				{
				setState(273);
				modelNewEntity();
				}
				break;
			case T__24:
				enterOuterAlt(_localctx, 2);
				{
				setState(274);
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
			setState(277);
			match(T__23);
			setState(278);
			((ModelNewEntityContext)_localctx).type = match(ID);
			setState(290);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(279);
				match(T__13);
				{
				{
				setState(280);
				((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
				((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
				}
				setState(285);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(281);
					match(T__11);
					setState(282);
					((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
					((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
					}
					}
					setState(287);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(288);
				match(T__14);
				}
			}

			setState(292);
			match(T__3);
			setState(296);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__17) | (1L << T__23) | (1L << ID) | (1L << STRING))) != 0)) {
				{
				{
				setState(293);
				((ModelNewEntityContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityContext)_localctx).decl.add(((ModelNewEntityContext)_localctx).modelValueType);
				}
				}
				setState(298);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(299);
			match(T__4);
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
			setState(304);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(301);
				modelAttribut();
				}
				break;
			case 2:
				{
				setState(302);
				modelEntityAsAttribut();
				}
				break;
			case 3:
				{
				setState(303);
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
			setState(307);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(306);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(309);
			match(T__17);
			}
			{
			{
			setState(310);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(311);
				match(T__11);
				setState(312);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(317);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(318);
			match(T__18);
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(321);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(320);
				((ModelEntityAsAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(323);
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
			setState(326);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(325);
				((ModelAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(328);
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
			setState(330);
			match(T__24);
			setState(331);
			((ModelSetEntityContext)_localctx).name = match(ID);
			setState(343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(332);
				match(T__13);
				{
				{
				setState(333);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				setState(338);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__11) {
					{
					{
					setState(334);
					match(T__11);
					setState(335);
					((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
					((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
					}
					}
					setState(340);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(341);
				match(T__14);
				}
			}

			setState(345);
			match(T__3);
			setState(349);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__25) | (1L << ID))) != 0)) {
				{
				{
				setState(346);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).attrs.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				}
				setState(351);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(352);
			match(T__4);
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
			setState(355);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,44,_ctx) ) {
			case 1:
				{
				setState(354);
				((ModelSetAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(357);
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
			setState(362);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(359);
				modelSetType();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 2);
				{
				setState(360);
				modelSetFuncDef();
				}
				break;
			case T__25:
				enterOuterAlt(_localctx, 3);
				{
				setState(361);
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
		public ModelGenericContext generic;
		public Token array;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelGenericContext modelGeneric() {
			return getRuleContext(ModelGenericContext.class,0);
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
			setState(364);
			((ModelSetTypeContext)_localctx).type = match(ID);
			setState(369);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(365);
				match(T__15);
				{
				setState(366);
				((ModelSetTypeContext)_localctx).generic = modelGeneric();
				}
				setState(367);
				match(T__16);
				}
			}

			setState(373);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__17) {
				{
				setState(371);
				((ModelSetTypeContext)_localctx).array = match(T__17);
				setState(372);
				match(T__18);
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

	public static class ModelGenericContext extends ParserRuleContext {
		public ModelSetTypeContext modelSetType;
		public List<ModelSetTypeContext> types = new ArrayList<ModelSetTypeContext>();
		public List<ModelSetTypeContext> modelSetType() {
			return getRuleContexts(ModelSetTypeContext.class);
		}
		public ModelSetTypeContext modelSetType(int i) {
			return getRuleContext(ModelSetTypeContext.class,i);
		}
		public ModelGenericContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelGeneric; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelGeneric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelGeneric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelGenericContext modelGeneric() throws RecognitionException {
		ModelGenericContext _localctx = new ModelGenericContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_modelGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(375);
			((ModelGenericContext)_localctx).modelSetType = modelSetType();
			((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(376);
				match(T__11);
				setState(377);
				((ModelGenericContext)_localctx).modelSetType = modelSetType();
				((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
				}
				}
				setState(382);
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
		enterRule(_localctx, 62, RULE_modelSetFuncDef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			match(T__13);
			setState(384);
			match(T__14);
			setState(394);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,50,_ctx) ) {
			case 1:
				{
				setState(385);
				match(T__25);
				setState(386);
				((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
				((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
				setState(391);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(387);
						match(T__11);
						setState(388);
						((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
						((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
						}
						} 
					}
					setState(393);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,49,_ctx);
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
		enterRule(_localctx, 64, RULE_modelSetRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(396);
			match(T__25);
			setState(397);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!\u0192\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\3\2\5\2F\n\2\3\2\5\2I\n\2\3\2\7\2L\n\2\f\2\16\2O\13\2\3\2"+
		"\7\2R\n\2\f\2\16\2U\13\2\3\2\7\2X\n\2\f\2\16\2[\13\2\3\3\3\3\3\3\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\7\5f\n\5\f\5\16\5i\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\7\3\7\3\7\5\7u\n\7\3\7\7\7x\n\7\f\7\16\7{\13\7\3\7\7\7~\n\7\f\7\16"+
		"\7\u0081\13\7\3\7\7\7\u0084\n\7\f\7\16\7\u0087\13\7\3\7\3\7\3\b\3\b\3"+
		"\b\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\7\n\u0098\n\n\f\n\16\n\u009b"+
		"\13\n\5\n\u009d\n\n\3\n\3\n\7\n\u00a1\n\n\f\n\16\n\u00a4\13\n\3\n\3\n"+
		"\3\13\3\13\5\13\u00aa\n\13\3\f\3\f\3\f\7\f\u00af\n\f\f\f\16\f\u00b2\13"+
		"\f\3\f\3\f\3\f\3\f\7\f\u00b8\n\f\f\f\16\f\u00bb\13\f\5\f\u00bd\n\f\3\f"+
		"\3\f\7\f\u00c1\n\f\f\f\16\f\u00c4\13\f\3\f\5\f\u00c7\n\f\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\7\16\u00d0\n\16\f\16\16\16\u00d3\13\16\5\16\u00d5\n"+
		"\16\3\17\5\17\u00d8\n\17\3\17\3\17\3\17\5\17\u00dd\n\17\3\20\3\20\3\20"+
		"\3\20\3\20\5\20\u00e4\n\20\3\20\3\20\5\20\u00e8\n\20\3\21\3\21\3\21\7"+
		"\21\u00ed\n\21\f\21\16\21\u00f0\13\21\3\22\3\22\5\22\u00f4\n\22\3\23\3"+
		"\23\3\23\3\23\5\23\u00fa\n\23\3\23\3\23\5\23\u00fe\n\23\3\24\3\24\3\24"+
		"\3\24\5\24\u0104\n\24\3\24\3\24\5\24\u0108\n\24\3\25\3\25\3\25\7\25\u010d"+
		"\n\25\f\25\16\25\u0110\13\25\3\25\3\25\3\26\3\26\5\26\u0116\n\26\3\27"+
		"\3\27\3\27\3\27\3\27\3\27\7\27\u011e\n\27\f\27\16\27\u0121\13\27\3\27"+
		"\3\27\5\27\u0125\n\27\3\27\3\27\7\27\u0129\n\27\f\27\16\27\u012c\13\27"+
		"\3\27\3\27\3\30\3\30\3\30\5\30\u0133\n\30\3\31\5\31\u0136\n\31\3\31\3"+
		"\31\3\31\3\31\7\31\u013c\n\31\f\31\16\31\u013f\13\31\3\31\3\31\3\32\5"+
		"\32\u0144\n\32\3\32\3\32\3\33\5\33\u0149\n\33\3\33\3\33\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\7\34\u0153\n\34\f\34\16\34\u0156\13\34\3\34\3\34\5\34"+
		"\u015a\n\34\3\34\3\34\7\34\u015e\n\34\f\34\16\34\u0161\13\34\3\34\3\34"+
		"\3\35\5\35\u0166\n\35\3\35\3\35\3\36\3\36\3\36\5\36\u016d\n\36\3\37\3"+
		"\37\3\37\3\37\3\37\5\37\u0174\n\37\3\37\3\37\5\37\u0178\n\37\3 \3 \3 "+
		"\7 \u017d\n \f \16 \u0180\13 \3!\3!\3!\3!\3!\3!\7!\u0188\n!\f!\16!\u018b"+
		"\13!\5!\u018d\n!\3\"\3\"\3\"\3\"\2\2#\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668:<>@B\2\2\2\u01a5\2E\3\2\2\2\4\\\3\2\2\2\6"+
		"_\3\2\2\2\bb\3\2\2\2\nl\3\2\2\2\fq\3\2\2\2\16\u008a\3\2\2\2\20\u008d\3"+
		"\2\2\2\22\u0090\3\2\2\2\24\u00a9\3\2\2\2\26\u00ab\3\2\2\2\30\u00c8\3\2"+
		"\2\2\32\u00d4\3\2\2\2\34\u00d7\3\2\2\2\36\u00de\3\2\2\2 \u00e9\3\2\2\2"+
		"\"\u00f3\3\2\2\2$\u00f5\3\2\2\2&\u00ff\3\2\2\2(\u0109\3\2\2\2*\u0115\3"+
		"\2\2\2,\u0117\3\2\2\2.\u0132\3\2\2\2\60\u0135\3\2\2\2\62\u0143\3\2\2\2"+
		"\64\u0148\3\2\2\2\66\u014c\3\2\2\28\u0165\3\2\2\2:\u016c\3\2\2\2<\u016e"+
		"\3\2\2\2>\u0179\3\2\2\2@\u0181\3\2\2\2B\u018e\3\2\2\2DF\5\4\3\2ED\3\2"+
		"\2\2EF\3\2\2\2FH\3\2\2\2GI\5\6\4\2HG\3\2\2\2HI\3\2\2\2IM\3\2\2\2JL\5\b"+
		"\5\2KJ\3\2\2\2LO\3\2\2\2MK\3\2\2\2MN\3\2\2\2NS\3\2\2\2OM\3\2\2\2PR\5\f"+
		"\7\2QP\3\2\2\2RU\3\2\2\2SQ\3\2\2\2ST\3\2\2\2TY\3\2\2\2US\3\2\2\2VX\5("+
		"\25\2WV\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\3\3\2\2\2[Y\3\2\2\2\\]"+
		"\7\3\2\2]^\7 \2\2^\5\3\2\2\2_`\7\4\2\2`a\7 \2\2a\7\3\2\2\2bc\7\5\2\2c"+
		"g\7\6\2\2df\5\n\6\2ed\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2\2\2hj\3\2\2\2"+
		"ig\3\2\2\2jk\7\7\2\2k\t\3\2\2\2lm\7\b\2\2mn\7\36\2\2no\7\6\2\2op\7\7\2"+
		"\2p\13\3\2\2\2qr\7\t\2\2rt\7\6\2\2su\5\16\b\2ts\3\2\2\2tu\3\2\2\2uy\3"+
		"\2\2\2vx\5\20\t\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2\2\2z\177\3\2\2\2"+
		"{y\3\2\2\2|~\5\22\n\2}|\3\2\2\2~\u0081\3\2\2\2\177}\3\2\2\2\177\u0080"+
		"\3\2\2\2\u0080\u0085\3\2\2\2\u0081\177\3\2\2\2\u0082\u0084\5\26\f\2\u0083"+
		"\u0082\3\2\2\2\u0084\u0087\3\2\2\2\u0085\u0083\3\2\2\2\u0085\u0086\3\2"+
		"\2\2\u0086\u0088\3\2\2\2\u0087\u0085\3\2\2\2\u0088\u0089\7\7\2\2\u0089"+
		"\r\3\2\2\2\u008a\u008b\7\n\2\2\u008b\u008c\7 \2\2\u008c\17\3\2\2\2\u008d"+
		"\u008e\7\13\2\2\u008e\u008f\7 \2\2\u008f\21\3\2\2\2\u0090\u0091\7\f\2"+
		"\2\u0091\u009c\7\36\2\2\u0092\u0093\7\r\2\2\u0093\u0094\7\36\2\2\u0094"+
		"\u0099\3\2\2\2\u0095\u0096\7\16\2\2\u0096\u0098\7\36\2\2\u0097\u0095\3"+
		"\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a"+
		"\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u0092\3\2\2\2\u009c\u009d\3\2"+
		"\2\2\u009d\u009e\3\2\2\2\u009e\u00a2\7\6\2\2\u009f\u00a1\5\24\13\2\u00a0"+
		"\u009f\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2"+
		"\2\2\u00a3\u00a5\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a6\7\7\2\2\u00a6"+
		"\23\3\2\2\2\u00a7\u00aa\5\"\22\2\u00a8\u00aa\5\26\f\2\u00a9\u00a7\3\2"+
		"\2\2\u00a9\u00a8\3\2\2\2\u00aa\25\3\2\2\2\u00ab\u00ac\7\b\2\2\u00ac\u00b0"+
		"\7\36\2\2\u00ad\u00af\5\30\r\2\u00ae\u00ad\3\2\2\2\u00af\u00b2\3\2\2\2"+
		"\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00bc\3\2\2\2\u00b2\u00b0"+
		"\3\2\2\2\u00b3\u00b4\7\17\2\2\u00b4\u00b9\5\36\20\2\u00b5\u00b6\7\16\2"+
		"\2\u00b6\u00b8\5\36\20\2\u00b7\u00b5\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bd\3\2\2\2\u00bb\u00b9\3\2"+
		"\2\2\u00bc\u00b3\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\u00c6\3\2\2\2\u00be"+
		"\u00c2\7\6\2\2\u00bf\u00c1\5\"\22\2\u00c0\u00bf\3\2\2\2\u00c1\u00c4\3"+
		"\2\2\2\u00c2\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c5\3\2\2\2\u00c4"+
		"\u00c2\3\2\2\2\u00c5\u00c7\7\7\2\2\u00c6\u00be\3\2\2\2\u00c6\u00c7\3\2"+
		"\2\2\u00c7\27\3\2\2\2\u00c8\u00c9\7\20\2\2\u00c9\u00ca\5\32\16\2\u00ca"+
		"\u00cb\7\21\2\2\u00cb\31\3\2\2\2\u00cc\u00d1\5\34\17\2\u00cd\u00ce\7\16"+
		"\2\2\u00ce\u00d0\5\34\17\2\u00cf\u00cd\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2"+
		"\2\2\u00d4\u00cc\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\33\3\2\2\2\u00d6\u00d8"+
		"\7\36\2\2\u00d7\u00d6\3\2\2\2\u00d7\u00d8\3\2\2\2\u00d8\u00d9\3\2\2\2"+
		"\u00d9\u00dc\7\36\2\2\u00da\u00db\7\17\2\2\u00db\u00dd\5\36\20\2\u00dc"+
		"\u00da\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\35\3\2\2\2\u00de\u00e3\7\36\2"+
		"\2\u00df\u00e0\7\22\2\2\u00e0\u00e1\5 \21\2\u00e1\u00e2\7\23\2\2\u00e2"+
		"\u00e4\3\2\2\2\u00e3\u00df\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e7\3\2"+
		"\2\2\u00e5\u00e6\7\24\2\2\u00e6\u00e8\7\25\2\2\u00e7\u00e5\3\2\2\2\u00e7"+
		"\u00e8\3\2\2\2\u00e8\37\3\2\2\2\u00e9\u00ee\5\36\20\2\u00ea\u00eb\7\16"+
		"\2\2\u00eb\u00ed\5\36\20\2\u00ec\u00ea\3\2\2\2\u00ed\u00f0\3\2\2\2\u00ee"+
		"\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef!\3\2\2\2\u00f0\u00ee\3\2\2\2"+
		"\u00f1\u00f4\5$\23\2\u00f2\u00f4\5&\24\2\u00f3\u00f1\3\2\2\2\u00f3\u00f2"+
		"\3\2\2\2\u00f4#\3\2\2\2\u00f5\u00f6\7\26\2\2\u00f6\u00f9\7\36\2\2\u00f7"+
		"\u00f8\7\17\2\2\u00f8\u00fa\5\36\20\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa"+
		"\3\2\2\2\u00fa\u00fd\3\2\2\2\u00fb\u00fc\7\27\2\2\u00fc\u00fe\5\"\22\2"+
		"\u00fd\u00fb\3\2\2\2\u00fd\u00fe\3\2\2\2\u00fe%\3\2\2\2\u00ff\u0100\7"+
		"\30\2\2\u0100\u0103\7\36\2\2\u0101\u0102\7\17\2\2\u0102\u0104\5\36\20"+
		"\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0106"+
		"\7\27\2\2\u0106\u0108\5\"\22\2\u0107\u0105\3\2\2\2\u0107\u0108\3\2\2\2"+
		"\u0108\'\3\2\2\2\u0109\u010a\7\31\2\2\u010a\u010e\7\6\2\2\u010b\u010d"+
		"\5*\26\2\u010c\u010b\3\2\2\2\u010d\u0110\3\2\2\2\u010e\u010c\3\2\2\2\u010e"+
		"\u010f\3\2\2\2\u010f\u0111\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0112\7\7"+
		"\2\2\u0112)\3\2\2\2\u0113\u0116\5,\27\2\u0114\u0116\5\66\34\2\u0115\u0113"+
		"\3\2\2\2\u0115\u0114\3\2\2\2\u0116+\3\2\2\2\u0117\u0118\7\32\2\2\u0118"+
		"\u0124\7\36\2\2\u0119\u011a\7\20\2\2\u011a\u011f\5\64\33\2\u011b\u011c"+
		"\7\16\2\2\u011c\u011e\5\64\33\2\u011d\u011b\3\2\2\2\u011e\u0121\3\2\2"+
		"\2\u011f\u011d\3\2\2\2\u011f\u0120\3\2\2\2\u0120\u0122\3\2\2\2\u0121\u011f"+
		"\3\2\2\2\u0122\u0123\7\21\2\2\u0123\u0125\3\2\2\2\u0124\u0119\3\2\2\2"+
		"\u0124\u0125\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u012a\7\6\2\2\u0127\u0129"+
		"\5.\30\2\u0128\u0127\3\2\2\2\u0129\u012c\3\2\2\2\u012a\u0128\3\2\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012d\3\2\2\2\u012c\u012a\3\2\2\2\u012d\u012e\7\7"+
		"\2\2\u012e-\3\2\2\2\u012f\u0133\5\64\33\2\u0130\u0133\5\62\32\2\u0131"+
		"\u0133\5\60\31\2\u0132\u012f\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0131\3"+
		"\2\2\2\u0133/\3\2\2\2\u0134\u0136\7\36\2\2\u0135\u0134\3\2\2\2\u0135\u0136"+
		"\3\2\2\2\u0136\u0137\3\2\2\2\u0137\u0138\7\24\2\2\u0138\u013d\5.\30\2"+
		"\u0139\u013a\7\16\2\2\u013a\u013c\5.\30\2\u013b\u0139\3\2\2\2\u013c\u013f"+
		"\3\2\2\2\u013d\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e\u0140\3\2\2\2\u013f"+
		"\u013d\3\2\2\2\u0140\u0141\7\25\2\2\u0141\61\3\2\2\2\u0142\u0144\7\36"+
		"\2\2\u0143\u0142\3\2\2\2\u0143\u0144\3\2\2\2\u0144\u0145\3\2\2\2\u0145"+
		"\u0146\5,\27\2\u0146\63\3\2\2\2\u0147\u0149\7\36\2\2\u0148\u0147\3\2\2"+
		"\2\u0148\u0149\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\7 \2\2\u014b\65"+
		"\3\2\2\2\u014c\u014d\7\33\2\2\u014d\u0159\7\36\2\2\u014e\u014f\7\20\2"+
		"\2\u014f\u0154\58\35\2\u0150\u0151\7\16\2\2\u0151\u0153\58\35\2\u0152"+
		"\u0150\3\2\2\2\u0153\u0156\3\2\2\2\u0154\u0152\3\2\2\2\u0154\u0155\3\2"+
		"\2\2\u0155\u0157\3\2\2\2\u0156\u0154\3\2\2\2\u0157\u0158\7\21\2\2\u0158"+
		"\u015a\3\2\2\2\u0159\u014e\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015b\3\2"+
		"\2\2\u015b\u015f\7\6\2\2\u015c\u015e\58\35\2\u015d\u015c\3\2\2\2\u015e"+
		"\u0161\3\2\2\2\u015f\u015d\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0162\3\2"+
		"\2\2\u0161\u015f\3\2\2\2\u0162\u0163\7\7\2\2\u0163\67\3\2\2\2\u0164\u0166"+
		"\7\36\2\2\u0165\u0164\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0167\3\2\2\2"+
		"\u0167\u0168\5:\36\2\u01689\3\2\2\2\u0169\u016d\5<\37\2\u016a\u016d\5"+
		"@!\2\u016b\u016d\5B\"\2\u016c\u0169\3\2\2\2\u016c\u016a\3\2\2\2\u016c"+
		"\u016b\3\2\2\2\u016d;\3\2\2\2\u016e\u0173\7\36\2\2\u016f\u0170\7\22\2"+
		"\2\u0170\u0171\5> \2\u0171\u0172\7\23\2\2\u0172\u0174\3\2\2\2\u0173\u016f"+
		"\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0177\3\2\2\2\u0175\u0176\7\24\2\2"+
		"\u0176\u0178\7\25\2\2\u0177\u0175\3\2\2\2\u0177\u0178\3\2\2\2\u0178=\3"+
		"\2\2\2\u0179\u017e\5<\37\2\u017a\u017b\7\16\2\2\u017b\u017d\5<\37\2\u017c"+
		"\u017a\3\2\2\2\u017d\u0180\3\2\2\2\u017e\u017c\3\2\2\2\u017e\u017f\3\2"+
		"\2\2\u017f?\3\2\2\2\u0180\u017e\3\2\2\2\u0181\u0182\7\20\2\2\u0182\u018c"+
		"\7\21\2\2\u0183\u0184\7\34\2\2\u0184\u0189\5<\37\2\u0185\u0186\7\16\2"+
		"\2\u0186\u0188\5<\37\2\u0187\u0185\3\2\2\2\u0188\u018b\3\2\2\2\u0189\u0187"+
		"\3\2\2\2\u0189\u018a\3\2\2\2\u018a\u018d\3\2\2\2\u018b\u0189\3\2\2\2\u018c"+
		"\u0183\3\2\2\2\u018c\u018d\3\2\2\2\u018dA\3\2\2\2\u018e\u018f\7\34\2\2"+
		"\u018f\u0190\7\36\2\2\u0190C\3\2\2\2\65EHMSYgty\177\u0085\u0099\u009c"+
		"\u00a2\u00a9\u00b0\u00b9\u00bc\u00c2\u00c6\u00d1\u00d4\u00d7\u00dc\u00e3"+
		"\u00e7\u00ee\u00f3\u00f9\u00fd\u0103\u0107\u010e\u0115\u011f\u0124\u012a"+
		"\u0132\u0135\u013d\u0143\u0148\u0154\u0159\u015f\u0165\u016c\u0173\u0177"+
		"\u017e\u0189\u018c";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}