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
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, TEXT=33, ID=34, WS=35, STRING=36, NUMBER=37;
	public static final int
		RULE_domainModel = 0, RULE_domainBlock = 1, RULE_tmplBlock = 2, RULE_tmplPkg = 3, 
		RULE_tmplUse = 4, RULE_tmplImpl = 5, RULE_tmplImplContent = 6, RULE_tmplFunc = 7, 
		RULE_tmplCurrying = 8, RULE_tmplCurryingParam = 9, RULE_tmplParam = 10, 
		RULE_tmplType = 11, RULE_tmplGeneric = 12, RULE_tmplExpression = 13, RULE_tmplVal = 14, 
		RULE_tmplVar = 15, RULE_modelBlock = 16, RULE_modelContent = 17, RULE_modelNewEntity = 18, 
		RULE_modelNewEntityValue = 19, RULE_modelValueType = 20, RULE_modelTbl = 21, 
		RULE_modelEntityAsAttribute = 22, RULE_modelAttribute = 23, RULE_modelSetEntity = 24, 
		RULE_modelSetAttribute = 25, RULE_modelSetValueType = 26, RULE_modelSetType = 27, 
		RULE_modelGeneric = 28, RULE_modelSetFuncDef = 29, RULE_modelSetRef = 30, 
		RULE_helperBlock = 31, RULE_helperFunc = 32, RULE_helperContent = 33, 
		RULE_helperStatement = 34, RULE_helperIf = 35, RULE_helperElse = 36, RULE_helperCondition = 37, 
		RULE_conditionMark = 38, RULE_helperFor = 39, RULE_helperCallObj = 40, 
		RULE_helperCallArray = 41, RULE_helperCallFunc = 42, RULE_helperCallVariable = 43;
	private static String[] makeRuleNames() {
		return new String[] {
			"domainModel", "domainBlock", "tmplBlock", "tmplPkg", "tmplUse", "tmplImpl", 
			"tmplImplContent", "tmplFunc", "tmplCurrying", "tmplCurryingParam", "tmplParam", 
			"tmplType", "tmplGeneric", "tmplExpression", "tmplVal", "tmplVar", "modelBlock", 
			"modelContent", "modelNewEntity", "modelNewEntityValue", "modelValueType", 
			"modelTbl", "modelEntityAsAttribute", "modelAttribute", "modelSetEntity", 
			"modelSetAttribute", "modelSetValueType", "modelSetType", "modelGeneric", 
			"modelSetFuncDef", "modelSetRef", "helperBlock", "helperFunc", "helperContent", 
			"helperStatement", "helperIf", "helperElse", "helperCondition", "conditionMark", 
			"helperFor", "helperCallObj", "helperCallArray", "helperCallFunc", "helperCallVariable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'tmpl'", "'{'", "'lang'", "'}'", "'pkg'", "'use'", "'impl'", "'for'", 
			"','", "'func'", "':'", "'('", "')'", "'<'", "'>'", "'['", "']'", "'val'", 
			"'='", "'var'", "'model'", "'let'", "'set'", "'->'", "'helper'", "'if'", 
			"'else'", "'=='", "'!='", "'<='", "'>='", "'in'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, "TEXT", "ID", "WS", 
			"STRING", "NUMBER"
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
		public DomainBlockContext domainBlock;
		public List<DomainBlockContext> body = new ArrayList<DomainBlockContext>();
		public List<DomainBlockContext> domainBlock() {
			return getRuleContexts(DomainBlockContext.class);
		}
		public DomainBlockContext domainBlock(int i) {
			return getRuleContext(DomainBlockContext.class,i);
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
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << T__20) | (1L << T__24))) != 0)) {
				{
				{
				setState(88);
				((DomainModelContext)_localctx).domainBlock = domainBlock();
				((DomainModelContext)_localctx).body.add(((DomainModelContext)_localctx).domainBlock);
				}
				}
				setState(93);
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

	public static class DomainBlockContext extends ParserRuleContext {
		public HelperBlockContext helperBlock() {
			return getRuleContext(HelperBlockContext.class,0);
		}
		public TmplBlockContext tmplBlock() {
			return getRuleContext(TmplBlockContext.class,0);
		}
		public ModelBlockContext modelBlock() {
			return getRuleContext(ModelBlockContext.class,0);
		}
		public DomainBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domainBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterDomainBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitDomainBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitDomainBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DomainBlockContext domainBlock() throws RecognitionException {
		DomainBlockContext _localctx = new DomainBlockContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_domainBlock);
		try {
			setState(97);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__24:
				enterOuterAlt(_localctx, 1);
				{
				setState(94);
				helperBlock();
				}
				break;
			case T__0:
				enterOuterAlt(_localctx, 2);
				{
				setState(95);
				tmplBlock();
				}
				break;
			case T__20:
				enterOuterAlt(_localctx, 3);
				{
				setState(96);
				modelBlock();
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

	public static class TmplBlockContext extends ParserRuleContext {
		public Token lang;
		public TmplPkgContext tmplPakage;
		public TmplUseContext tmplUse;
		public List<TmplUseContext> tmplUses = new ArrayList<TmplUseContext>();
		public TmplImplContext tmplImpl;
		public List<TmplImplContext> tmplImpls = new ArrayList<TmplImplContext>();
		public TmplFuncContext tmplFunc;
		public List<TmplFuncContext> tmplFuncs = new ArrayList<TmplFuncContext>();
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
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
		enterRule(_localctx, 4, RULE_tmplBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(T__0);
			setState(100);
			match(T__1);
			{
			setState(101);
			match(T__2);
			setState(102);
			((TmplBlockContext)_localctx).lang = match(STRING);
			}
			setState(105);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(104);
				((TmplBlockContext)_localctx).tmplPakage = tmplPkg();
				}
			}

			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(107);
				((TmplBlockContext)_localctx).tmplUse = tmplUse();
				((TmplBlockContext)_localctx).tmplUses.add(((TmplBlockContext)_localctx).tmplUse);
				}
				}
				setState(112);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(113);
				((TmplBlockContext)_localctx).tmplImpl = tmplImpl();
				((TmplBlockContext)_localctx).tmplImpls.add(((TmplBlockContext)_localctx).tmplImpl);
				}
				}
				setState(118);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(122);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(119);
				((TmplBlockContext)_localctx).tmplFunc = tmplFunc();
				((TmplBlockContext)_localctx).tmplFuncs.add(((TmplBlockContext)_localctx).tmplFunc);
				}
				}
				setState(124);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(125);
			match(T__3);
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
		enterRule(_localctx, 6, RULE_tmplPkg);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			match(T__4);
			setState(128);
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
		enterRule(_localctx, 8, RULE_tmplUse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			match(T__5);
			setState(131);
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
		enterRule(_localctx, 10, RULE_tmplImpl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(133);
			match(T__6);
			setState(134);
			((TmplImplContext)_localctx).name = match(ID);
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				{
				setState(135);
				match(T__7);
				setState(136);
				((TmplImplContext)_localctx).forName = match(ID);
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(138);
					match(T__8);
					setState(139);
					((TmplImplContext)_localctx).ID = match(ID);
					((TmplImplContext)_localctx).forNames.add(((TmplImplContext)_localctx).ID);
					}
					}
					setState(144);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(147);
			match(T__1);
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__9) | (1L << T__17) | (1L << T__19))) != 0)) {
				{
				{
				setState(148);
				((TmplImplContext)_localctx).tmplImplContent = tmplImplContent();
				((TmplImplContext)_localctx).tmplImplContents.add(((TmplImplContext)_localctx).tmplImplContent);
				}
				}
				setState(153);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(154);
			match(T__3);
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
		enterRule(_localctx, 12, RULE_tmplImplContent);
		try {
			setState(158);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
			case T__19:
				enterOuterAlt(_localctx, 1);
				{
				setState(156);
				tmplExpression();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(157);
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
		enterRule(_localctx, 14, RULE_tmplFunc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(T__9);
			setState(161);
			((TmplFuncContext)_localctx).name = match(ID);
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__11) {
				{
				{
				setState(162);
				((TmplFuncContext)_localctx).tmplCurrying = tmplCurrying();
				((TmplFuncContext)_localctx).curries.add(((TmplFuncContext)_localctx).tmplCurrying);
				}
				}
				setState(167);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(168);
				match(T__10);
				setState(169);
				((TmplFuncContext)_localctx).tmplType = tmplType();
				((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
				setState(174);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(170);
					match(T__8);
					setState(171);
					((TmplFuncContext)_localctx).tmplType = tmplType();
					((TmplFuncContext)_localctx).types.add(((TmplFuncContext)_localctx).tmplType);
					}
					}
					setState(176);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(187);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__1) {
				{
				setState(179);
				match(T__1);
				setState(183);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__17 || _la==T__19) {
					{
					{
					setState(180);
					((TmplFuncContext)_localctx).tmplExpression = tmplExpression();
					((TmplFuncContext)_localctx).exprs.add(((TmplFuncContext)_localctx).tmplExpression);
					}
					}
					setState(185);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(186);
				match(T__3);
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
		enterRule(_localctx, 16, RULE_tmplCurrying);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(189);
			match(T__11);
			setState(190);
			((TmplCurryingContext)_localctx).param = tmplCurryingParam();
			setState(191);
			match(T__12);
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
		enterRule(_localctx, 18, RULE_tmplCurryingParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				{
				setState(193);
				((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
				((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
				}
				setState(198);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(194);
					match(T__8);
					setState(195);
					((TmplCurryingParamContext)_localctx).tmplParam = tmplParam();
					((TmplCurryingParamContext)_localctx).params.add(((TmplCurryingParamContext)_localctx).tmplParam);
					}
					}
					setState(200);
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
		enterRule(_localctx, 20, RULE_tmplParam);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				{
				setState(203);
				((TmplParamContext)_localctx).accessor = match(ID);
				}
				break;
			}
			setState(206);
			((TmplParamContext)_localctx).name = match(ID);
			setState(209);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(207);
				match(T__10);
				setState(208);
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
		enterRule(_localctx, 22, RULE_tmplType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			((TmplTypeContext)_localctx).type = match(ID);
			setState(216);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(212);
				match(T__13);
				{
				setState(213);
				((TmplTypeContext)_localctx).generic = tmplGeneric();
				}
				setState(214);
				match(T__14);
				}
			}

			setState(220);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(218);
				((TmplTypeContext)_localctx).array = match(T__15);
				setState(219);
				match(T__16);
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
		enterRule(_localctx, 24, RULE_tmplGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(222);
			((TmplGenericContext)_localctx).tmplType = tmplType();
			((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(223);
				match(T__8);
				setState(224);
				((TmplGenericContext)_localctx).tmplType = tmplType();
				((TmplGenericContext)_localctx).types.add(((TmplGenericContext)_localctx).tmplType);
				}
				}
				setState(229);
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
		enterRule(_localctx, 26, RULE_tmplExpression);
		try {
			setState(232);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__17:
				enterOuterAlt(_localctx, 1);
				{
				setState(230);
				tmplVal();
				}
				break;
			case T__19:
				enterOuterAlt(_localctx, 2);
				{
				setState(231);
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
		enterRule(_localctx, 28, RULE_tmplVal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			match(T__17);
			setState(235);
			((TmplValContext)_localctx).name = match(ID);
			setState(238);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(236);
				match(T__10);
				setState(237);
				((TmplValContext)_localctx).type = tmplType();
				}
			}

			setState(242);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__18) {
				{
				setState(240);
				match(T__18);
				setState(241);
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
		enterRule(_localctx, 30, RULE_tmplVar);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			match(T__19);
			setState(245);
			((TmplVarContext)_localctx).name = match(ID);
			setState(248);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(246);
				match(T__10);
				setState(247);
				((TmplVarContext)_localctx).type = tmplType();
				}
			}

			setState(252);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__18) {
				{
				setState(250);
				match(T__18);
				setState(251);
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
		enterRule(_localctx, 32, RULE_modelBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(254);
			match(T__20);
			setState(255);
			match(T__1);
			setState(259);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__21 || _la==T__22) {
				{
				{
				setState(256);
				((ModelBlockContext)_localctx).modelContent = modelContent();
				((ModelBlockContext)_localctx).modelContents.add(((ModelBlockContext)_localctx).modelContent);
				}
				}
				setState(261);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(262);
			match(T__3);
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
		enterRule(_localctx, 34, RULE_modelContent);
		try {
			setState(266);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__21:
				enterOuterAlt(_localctx, 1);
				{
				setState(264);
				modelNewEntity();
				}
				break;
			case T__22:
				enterOuterAlt(_localctx, 2);
				{
				setState(265);
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
		public Token name;
		public ModelNewEntityValueContext entity;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelNewEntityValueContext modelNewEntityValue() {
			return getRuleContext(ModelNewEntityValueContext.class,0);
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
		enterRule(_localctx, 36, RULE_modelNewEntity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268);
			match(T__21);
			setState(269);
			((ModelNewEntityContext)_localctx).name = match(ID);
			setState(270);
			((ModelNewEntityContext)_localctx).entity = modelNewEntityValue();
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

	public static class ModelNewEntityValueContext extends ParserRuleContext {
		public Token type;
		public ModelValueTypeContext modelValueType;
		public List<ModelValueTypeContext> attrs = new ArrayList<ModelValueTypeContext>();
		public List<ModelValueTypeContext> decl = new ArrayList<ModelValueTypeContext>();
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public List<ModelValueTypeContext> modelValueType() {
			return getRuleContexts(ModelValueTypeContext.class);
		}
		public ModelValueTypeContext modelValueType(int i) {
			return getRuleContext(ModelValueTypeContext.class,i);
		}
		public ModelNewEntityValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelNewEntityValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelNewEntityValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelNewEntityValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelNewEntityValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelNewEntityValueContext modelNewEntityValue() throws RecognitionException {
		ModelNewEntityValueContext _localctx = new ModelNewEntityValueContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_modelNewEntityValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(273);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(272);
				((ModelNewEntityValueContext)_localctx).type = match(ID);
				}
			}

			setState(286);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(275);
				match(T__11);
				{
				{
				setState(276);
				((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityValueContext)_localctx).attrs.add(((ModelNewEntityValueContext)_localctx).modelValueType);
				}
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(277);
					match(T__8);
					setState(278);
					((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
					((ModelNewEntityValueContext)_localctx).attrs.add(((ModelNewEntityValueContext)_localctx).modelValueType);
					}
					}
					setState(283);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(284);
				match(T__12);
				}
			}

			setState(288);
			match(T__1);
			setState(292);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__11) | (1L << T__15) | (1L << ID) | (1L << STRING))) != 0)) {
				{
				{
				setState(289);
				((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityValueContext)_localctx).decl.add(((ModelNewEntityValueContext)_localctx).modelValueType);
				}
				}
				setState(294);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(295);
			match(T__3);
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
		public ModelAttributeContext modelAttribute() {
			return getRuleContext(ModelAttributeContext.class,0);
		}
		public ModelEntityAsAttributeContext modelEntityAsAttribute() {
			return getRuleContext(ModelEntityAsAttributeContext.class,0);
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
		enterRule(_localctx, 40, RULE_modelValueType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(300);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,33,_ctx) ) {
			case 1:
				{
				setState(297);
				modelAttribute();
				}
				break;
			case 2:
				{
				setState(298);
				modelEntityAsAttribute();
				}
				break;
			case 3:
				{
				setState(299);
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
		enterRule(_localctx, 42, RULE_modelTbl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(302);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(305);
			match(T__15);
			}
			{
			{
			setState(306);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(307);
				match(T__8);
				setState(308);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(313);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(314);
			match(T__16);
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

	public static class ModelEntityAsAttributeContext extends ParserRuleContext {
		public Token attr;
		public ModelNewEntityValueContext value;
		public ModelNewEntityValueContext modelNewEntityValue() {
			return getRuleContext(ModelNewEntityValueContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelEntityAsAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelEntityAsAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelEntityAsAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelEntityAsAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelEntityAsAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelEntityAsAttributeContext modelEntityAsAttribute() throws RecognitionException {
		ModelEntityAsAttributeContext _localctx = new ModelEntityAsAttributeContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_modelEntityAsAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(317);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
			case 1:
				{
				setState(316);
				((ModelEntityAsAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(319);
			((ModelEntityAsAttributeContext)_localctx).value = modelNewEntityValue();
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

	public static class ModelAttributeContext extends ParserRuleContext {
		public Token attr;
		public Token value;
		public TerminalNode STRING() { return getToken(TLangParser.STRING, 0); }
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public ModelAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterModelAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitModelAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitModelAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelAttributeContext modelAttribute() throws RecognitionException {
		ModelAttributeContext _localctx = new ModelAttributeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_modelAttribute);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(322);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(321);
				((ModelAttributeContext)_localctx).attr = match(ID);
				}
			}

			setState(324);
			((ModelAttributeContext)_localctx).value = match(STRING);
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
		enterRule(_localctx, 48, RULE_modelSetEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(T__22);
			setState(327);
			((ModelSetEntityContext)_localctx).name = match(ID);
			setState(339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(328);
				match(T__11);
				{
				{
				setState(329);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				setState(334);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(330);
					match(T__8);
					setState(331);
					((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
					((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
					}
					}
					setState(336);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(337);
				match(T__12);
				}
			}

			setState(341);
			match(T__1);
			setState(345);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__23) | (1L << ID))) != 0)) {
				{
				{
				setState(342);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).attrs.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				}
				setState(347);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(348);
			match(T__3);
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
		enterRule(_localctx, 50, RULE_modelSetAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,41,_ctx) ) {
			case 1:
				{
				setState(350);
				((ModelSetAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(353);
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
		enterRule(_localctx, 52, RULE_modelSetValueType);
		try {
			setState(358);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(355);
				modelSetType();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 2);
				{
				setState(356);
				modelSetFuncDef();
				}
				break;
			case T__23:
				enterOuterAlt(_localctx, 3);
				{
				setState(357);
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
		enterRule(_localctx, 54, RULE_modelSetType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			((ModelSetTypeContext)_localctx).type = match(ID);
			setState(365);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(361);
				match(T__13);
				{
				setState(362);
				((ModelSetTypeContext)_localctx).generic = modelGeneric();
				}
				setState(363);
				match(T__14);
				}
			}

			setState(369);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__15) {
				{
				setState(367);
				((ModelSetTypeContext)_localctx).array = match(T__15);
				setState(368);
				match(T__16);
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
		enterRule(_localctx, 56, RULE_modelGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(371);
			((ModelGenericContext)_localctx).modelSetType = modelSetType();
			((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
			setState(376);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(372);
				match(T__8);
				setState(373);
				((ModelGenericContext)_localctx).modelSetType = modelSetType();
				((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
				}
				}
				setState(378);
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
		enterRule(_localctx, 58, RULE_modelSetFuncDef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(379);
			match(T__11);
			setState(380);
			match(T__12);
			setState(390);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,47,_ctx) ) {
			case 1:
				{
				setState(381);
				match(T__23);
				setState(382);
				((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
				((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
				setState(387);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(383);
						match(T__8);
						setState(384);
						((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
						((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
						}
						} 
					}
					setState(389);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,46,_ctx);
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
		enterRule(_localctx, 60, RULE_modelSetRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(T__23);
			setState(393);
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
		enterRule(_localctx, 62, RULE_helperBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(395);
			match(T__24);
			setState(396);
			match(T__1);
			setState(400);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(397);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(402);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(403);
			match(T__3);
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
		public HelperContentContext body;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
		}
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
		enterRule(_localctx, 64, RULE_helperFunc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(405);
			match(T__9);
			setState(406);
			((HelperFuncContext)_localctx).name = match(ID);
			setState(407);
			match(T__1);
			setState(408);
			((HelperFuncContext)_localctx).body = helperContent();
			setState(409);
			match(T__3);
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

	public static class HelperContentContext extends ParserRuleContext {
		public HelperStatementContext helperStatement;
		public List<HelperStatementContext> content = new ArrayList<HelperStatementContext>();
		public List<HelperStatementContext> helperStatement() {
			return getRuleContexts(HelperStatementContext.class);
		}
		public HelperStatementContext helperStatement(int i) {
			return getRuleContext(HelperStatementContext.class,i);
		}
		public HelperContentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperContent; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperContentContext helperContent() throws RecognitionException {
		HelperContentContext _localctx = new HelperContentContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_helperContent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__25) | (1L << ID))) != 0)) {
				{
				{
				setState(411);
				((HelperContentContext)_localctx).helperStatement = helperStatement();
				((HelperContentContext)_localctx).content.add(((HelperContentContext)_localctx).helperStatement);
				}
				}
				setState(416);
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

	public static class HelperStatementContext extends ParserRuleContext {
		public HelperIfContext helperIf() {
			return getRuleContext(HelperIfContext.class,0);
		}
		public HelperForContext helperFor() {
			return getRuleContext(HelperForContext.class,0);
		}
		public HelperCallObjContext helperCallObj() {
			return getRuleContext(HelperCallObjContext.class,0);
		}
		public HelperStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperStatementContext helperStatement() throws RecognitionException {
		HelperStatementContext _localctx = new HelperStatementContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_helperStatement);
		try {
			setState(420);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__25:
				enterOuterAlt(_localctx, 1);
				{
				setState(417);
				helperIf();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(418);
				helperFor();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(419);
				helperCallObj();
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

	public static class HelperIfContext extends ParserRuleContext {
		public HelperConditionContext condition;
		public HelperContentContext body;
		public HelperElseContext orElse;
		public HelperConditionContext helperCondition() {
			return getRuleContext(HelperConditionContext.class,0);
		}
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
		}
		public HelperElseContext helperElse() {
			return getRuleContext(HelperElseContext.class,0);
		}
		public HelperIfContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperIf; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperIf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperIfContext helperIf() throws RecognitionException {
		HelperIfContext _localctx = new HelperIfContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_helperIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(422);
			match(T__25);
			setState(423);
			match(T__11);
			setState(424);
			((HelperIfContext)_localctx).condition = helperCondition();
			setState(425);
			match(T__12);
			setState(426);
			match(T__1);
			setState(427);
			((HelperIfContext)_localctx).body = helperContent();
			setState(428);
			match(T__3);
			setState(430);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__26) {
				{
				setState(429);
				((HelperIfContext)_localctx).orElse = helperElse();
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

	public static class HelperElseContext extends ParserRuleContext {
		public HelperContentContext body;
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
		}
		public HelperElseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperElse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperElse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperElse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperElse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperElseContext helperElse() throws RecognitionException {
		HelperElseContext _localctx = new HelperElseContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_helperElse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(432);
			match(T__26);
			setState(433);
			match(T__1);
			setState(434);
			((HelperElseContext)_localctx).body = helperContent();
			setState(435);
			match(T__3);
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

	public static class HelperConditionContext extends ParserRuleContext {
		public HelperCallObjContext arg1;
		public ConditionMarkContext mark;
		public HelperCallObjContext arg2;
		public List<HelperCallObjContext> helperCallObj() {
			return getRuleContexts(HelperCallObjContext.class);
		}
		public HelperCallObjContext helperCallObj(int i) {
			return getRuleContext(HelperCallObjContext.class,i);
		}
		public ConditionMarkContext conditionMark() {
			return getRuleContext(ConditionMarkContext.class,0);
		}
		public HelperConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCondition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperConditionContext helperCondition() throws RecognitionException {
		HelperConditionContext _localctx = new HelperConditionContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_helperCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(437);
			((HelperConditionContext)_localctx).arg1 = helperCallObj();
			setState(441);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__13) | (1L << T__14) | (1L << T__27) | (1L << T__28) | (1L << T__29))) != 0)) {
				{
				setState(438);
				((HelperConditionContext)_localctx).mark = conditionMark();
				setState(439);
				((HelperConditionContext)_localctx).arg2 = helperCallObj();
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

	public static class ConditionMarkContext extends ParserRuleContext {
		public ConditionMarkContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionMark; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterConditionMark(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitConditionMark(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitConditionMark(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionMarkContext conditionMark() throws RecognitionException {
		ConditionMarkContext _localctx = new ConditionMarkContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_conditionMark);
		try {
			setState(449);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__27:
				enterOuterAlt(_localctx, 1);
				{
				setState(443);
				match(T__27);
				}
				break;
			case T__28:
				enterOuterAlt(_localctx, 2);
				{
				setState(444);
				match(T__28);
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 3);
				{
				setState(445);
				match(T__13);
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 4);
				{
				setState(446);
				match(T__14);
				}
				break;
			case T__29:
				enterOuterAlt(_localctx, 5);
				{
				setState(447);
				match(T__29);
				setState(448);
				match(T__30);
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

	public static class HelperForContext extends ParserRuleContext {
		public Token var;
		public Token array;
		public HelperContentContext body;
		public List<TerminalNode> ID() { return getTokens(TLangParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangParser.ID, i);
		}
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
		}
		public HelperForContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperFor; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperFor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperForContext helperFor() throws RecognitionException {
		HelperForContext _localctx = new HelperForContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_helperFor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			match(T__7);
			setState(452);
			match(T__11);
			setState(453);
			((HelperForContext)_localctx).var = match(ID);
			setState(454);
			match(T__31);
			setState(455);
			((HelperForContext)_localctx).array = match(ID);
			setState(456);
			match(T__12);
			setState(457);
			match(T__1);
			setState(458);
			((HelperForContext)_localctx).body = helperContent();
			setState(459);
			match(T__3);
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

	public static class HelperCallObjContext extends ParserRuleContext {
		public HelperCallArrayContext helperCallArray() {
			return getRuleContext(HelperCallArrayContext.class,0);
		}
		public HelperCallFuncContext helperCallFunc() {
			return getRuleContext(HelperCallFuncContext.class,0);
		}
		public HelperCallVariableContext helperCallVariable() {
			return getRuleContext(HelperCallVariableContext.class,0);
		}
		public HelperCallObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallObj; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperCallObj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperCallObj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperCallObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallObjContext helperCallObj() throws RecognitionException {
		HelperCallObjContext _localctx = new HelperCallObjContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_helperCallObj);
		try {
			setState(464);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,54,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(461);
				helperCallArray();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(462);
				helperCallFunc();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(463);
				helperCallVariable();
				}
				break;
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

	public static class HelperCallArrayContext extends ParserRuleContext {
		public Token name;
		public Token elem;
		public List<TerminalNode> ID() { return getTokens(TLangParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangParser.ID, i);
		}
		public HelperCallArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperCallArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperCallArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperCallArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallArrayContext helperCallArray() throws RecognitionException {
		HelperCallArrayContext _localctx = new HelperCallArrayContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_helperCallArray);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(466);
			((HelperCallArrayContext)_localctx).name = match(ID);
			setState(467);
			match(T__15);
			setState(468);
			((HelperCallArrayContext)_localctx).elem = match(ID);
			setState(469);
			match(T__16);
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

	public static class HelperCallFuncContext extends ParserRuleContext {
		public Token name;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public HelperCallFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperCallFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperCallFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperCallFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallFuncContext helperCallFunc() throws RecognitionException {
		HelperCallFuncContext _localctx = new HelperCallFuncContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_helperCallFunc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(471);
			((HelperCallFuncContext)_localctx).name = match(ID);
			setState(472);
			match(T__11);
			setState(473);
			match(T__12);
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

	public static class HelperCallVariableContext extends ParserRuleContext {
		public Token name;
		public TerminalNode ID() { return getToken(TLangParser.ID, 0); }
		public HelperCallVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterHelperCallVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitHelperCallVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitHelperCallVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallVariableContext helperCallVariable() throws RecognitionException {
		HelperCallVariableContext _localctx = new HelperCallVariableContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_helperCallVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(475);
			((HelperCallVariableContext)_localctx).name = match(ID);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\'\u01e0\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\3\2\7\2\\\n\2\f\2\16\2_\13\2\3\3\3\3\3\3\5\3d\n\3\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\5\4l\n\4\3\4\7\4o\n\4\f\4\16\4r\13\4\3\4\7\4u\n\4\f\4\16"+
		"\4x\13\4\3\4\7\4{\n\4\f\4\16\4~\13\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\7\7\u008f\n\7\f\7\16\7\u0092\13\7\5\7\u0094"+
		"\n\7\3\7\3\7\7\7\u0098\n\7\f\7\16\7\u009b\13\7\3\7\3\7\3\b\3\b\5\b\u00a1"+
		"\n\b\3\t\3\t\3\t\7\t\u00a6\n\t\f\t\16\t\u00a9\13\t\3\t\3\t\3\t\3\t\7\t"+
		"\u00af\n\t\f\t\16\t\u00b2\13\t\5\t\u00b4\n\t\3\t\3\t\7\t\u00b8\n\t\f\t"+
		"\16\t\u00bb\13\t\3\t\5\t\u00be\n\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13"+
		"\u00c7\n\13\f\13\16\13\u00ca\13\13\5\13\u00cc\n\13\3\f\5\f\u00cf\n\f\3"+
		"\f\3\f\3\f\5\f\u00d4\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u00db\n\r\3\r\3\r\5\r"+
		"\u00df\n\r\3\16\3\16\3\16\7\16\u00e4\n\16\f\16\16\16\u00e7\13\16\3\17"+
		"\3\17\5\17\u00eb\n\17\3\20\3\20\3\20\3\20\5\20\u00f1\n\20\3\20\3\20\5"+
		"\20\u00f5\n\20\3\21\3\21\3\21\3\21\5\21\u00fb\n\21\3\21\3\21\5\21\u00ff"+
		"\n\21\3\22\3\22\3\22\7\22\u0104\n\22\f\22\16\22\u0107\13\22\3\22\3\22"+
		"\3\23\3\23\5\23\u010d\n\23\3\24\3\24\3\24\3\24\3\25\5\25\u0114\n\25\3"+
		"\25\3\25\3\25\3\25\7\25\u011a\n\25\f\25\16\25\u011d\13\25\3\25\3\25\5"+
		"\25\u0121\n\25\3\25\3\25\7\25\u0125\n\25\f\25\16\25\u0128\13\25\3\25\3"+
		"\25\3\26\3\26\3\26\5\26\u012f\n\26\3\27\5\27\u0132\n\27\3\27\3\27\3\27"+
		"\3\27\7\27\u0138\n\27\f\27\16\27\u013b\13\27\3\27\3\27\3\30\5\30\u0140"+
		"\n\30\3\30\3\30\3\31\5\31\u0145\n\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\7\32\u014f\n\32\f\32\16\32\u0152\13\32\3\32\3\32\5\32\u0156\n\32"+
		"\3\32\3\32\7\32\u015a\n\32\f\32\16\32\u015d\13\32\3\32\3\32\3\33\5\33"+
		"\u0162\n\33\3\33\3\33\3\34\3\34\3\34\5\34\u0169\n\34\3\35\3\35\3\35\3"+
		"\35\3\35\5\35\u0170\n\35\3\35\3\35\5\35\u0174\n\35\3\36\3\36\3\36\7\36"+
		"\u0179\n\36\f\36\16\36\u017c\13\36\3\37\3\37\3\37\3\37\3\37\3\37\7\37"+
		"\u0184\n\37\f\37\16\37\u0187\13\37\5\37\u0189\n\37\3 \3 \3 \3!\3!\3!\7"+
		"!\u0191\n!\f!\16!\u0194\13!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\3#\7#\u019f"+
		"\n#\f#\16#\u01a2\13#\3$\3$\3$\5$\u01a7\n$\3%\3%\3%\3%\3%\3%\3%\3%\5%\u01b1"+
		"\n%\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\5\'\u01bc\n\'\3(\3(\3(\3(\3(\3(\5("+
		"\u01c4\n(\3)\3)\3)\3)\3)\3)\3)\3)\3)\3)\3*\3*\3*\5*\u01d3\n*\3+\3+\3+"+
		"\3+\3+\3,\3,\3,\3,\3-\3-\3-\2\2.\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36"+
		" \"$&(*,.\60\62\64\668:<>@BDFHJLNPRTVX\2\2\2\u01f2\2]\3\2\2\2\4c\3\2\2"+
		"\2\6e\3\2\2\2\b\u0081\3\2\2\2\n\u0084\3\2\2\2\f\u0087\3\2\2\2\16\u00a0"+
		"\3\2\2\2\20\u00a2\3\2\2\2\22\u00bf\3\2\2\2\24\u00cb\3\2\2\2\26\u00ce\3"+
		"\2\2\2\30\u00d5\3\2\2\2\32\u00e0\3\2\2\2\34\u00ea\3\2\2\2\36\u00ec\3\2"+
		"\2\2 \u00f6\3\2\2\2\"\u0100\3\2\2\2$\u010c\3\2\2\2&\u010e\3\2\2\2(\u0113"+
		"\3\2\2\2*\u012e\3\2\2\2,\u0131\3\2\2\2.\u013f\3\2\2\2\60\u0144\3\2\2\2"+
		"\62\u0148\3\2\2\2\64\u0161\3\2\2\2\66\u0168\3\2\2\28\u016a\3\2\2\2:\u0175"+
		"\3\2\2\2<\u017d\3\2\2\2>\u018a\3\2\2\2@\u018d\3\2\2\2B\u0197\3\2\2\2D"+
		"\u01a0\3\2\2\2F\u01a6\3\2\2\2H\u01a8\3\2\2\2J\u01b2\3\2\2\2L\u01b7\3\2"+
		"\2\2N\u01c3\3\2\2\2P\u01c5\3\2\2\2R\u01d2\3\2\2\2T\u01d4\3\2\2\2V\u01d9"+
		"\3\2\2\2X\u01dd\3\2\2\2Z\\\5\4\3\2[Z\3\2\2\2\\_\3\2\2\2][\3\2\2\2]^\3"+
		"\2\2\2^\3\3\2\2\2_]\3\2\2\2`d\5@!\2ad\5\6\4\2bd\5\"\22\2c`\3\2\2\2ca\3"+
		"\2\2\2cb\3\2\2\2d\5\3\2\2\2ef\7\3\2\2fg\7\4\2\2gh\7\5\2\2hi\7&\2\2ik\3"+
		"\2\2\2jl\5\b\5\2kj\3\2\2\2kl\3\2\2\2lp\3\2\2\2mo\5\n\6\2nm\3\2\2\2or\3"+
		"\2\2\2pn\3\2\2\2pq\3\2\2\2qv\3\2\2\2rp\3\2\2\2su\5\f\7\2ts\3\2\2\2ux\3"+
		"\2\2\2vt\3\2\2\2vw\3\2\2\2w|\3\2\2\2xv\3\2\2\2y{\5\20\t\2zy\3\2\2\2{~"+
		"\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\177\3\2\2\2~|\3\2\2\2\177\u0080\7\6\2\2"+
		"\u0080\7\3\2\2\2\u0081\u0082\7\7\2\2\u0082\u0083\7&\2\2\u0083\t\3\2\2"+
		"\2\u0084\u0085\7\b\2\2\u0085\u0086\7&\2\2\u0086\13\3\2\2\2\u0087\u0088"+
		"\7\t\2\2\u0088\u0093\7$\2\2\u0089\u008a\7\n\2\2\u008a\u008b\7$\2\2\u008b"+
		"\u0090\3\2\2\2\u008c\u008d\7\13\2\2\u008d\u008f\7$\2\2\u008e\u008c\3\2"+
		"\2\2\u008f\u0092\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091"+
		"\u0094\3\2\2\2\u0092\u0090\3\2\2\2\u0093\u0089\3\2\2\2\u0093\u0094\3\2"+
		"\2\2\u0094\u0095\3\2\2\2\u0095\u0099\7\4\2\2\u0096\u0098\5\16\b\2\u0097"+
		"\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a\3\2"+
		"\2\2\u009a\u009c\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009d\7\6\2\2\u009d"+
		"\r\3\2\2\2\u009e\u00a1\5\34\17\2\u009f\u00a1\5\20\t\2\u00a0\u009e\3\2"+
		"\2\2\u00a0\u009f\3\2\2\2\u00a1\17\3\2\2\2\u00a2\u00a3\7\f\2\2\u00a3\u00a7"+
		"\7$\2\2\u00a4\u00a6\5\22\n\2\u00a5\u00a4\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00b3\3\2\2\2\u00a9\u00a7\3\2"+
		"\2\2\u00aa\u00ab\7\r\2\2\u00ab\u00b0\5\30\r\2\u00ac\u00ad\7\13\2\2\u00ad"+
		"\u00af\5\30\r\2\u00ae\u00ac\3\2\2\2\u00af\u00b2\3\2\2\2\u00b0\u00ae\3"+
		"\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3\2\2\2\u00b3"+
		"\u00aa\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4\u00bd\3\2\2\2\u00b5\u00b9\7\4"+
		"\2\2\u00b6\u00b8\5\34\17\2\u00b7\u00b6\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9"+
		"\u00b7\3\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bc\3\2\2\2\u00bb\u00b9\3\2"+
		"\2\2\u00bc\u00be\7\6\2\2\u00bd\u00b5\3\2\2\2\u00bd\u00be\3\2\2\2\u00be"+
		"\21\3\2\2\2\u00bf\u00c0\7\16\2\2\u00c0\u00c1\5\24\13\2\u00c1\u00c2\7\17"+
		"\2\2\u00c2\23\3\2\2\2\u00c3\u00c8\5\26\f\2\u00c4\u00c5\7\13\2\2\u00c5"+
		"\u00c7\5\26\f\2\u00c6\u00c4\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3"+
		"\2\2\2\u00c8\u00c9\3\2\2\2\u00c9\u00cc\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb"+
		"\u00c3\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\25\3\2\2\2\u00cd\u00cf\7$\2\2"+
		"\u00ce\u00cd\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d3"+
		"\7$\2\2\u00d1\u00d2\7\r\2\2\u00d2\u00d4\5\30\r\2\u00d3\u00d1\3\2\2\2\u00d3"+
		"\u00d4\3\2\2\2\u00d4\27\3\2\2\2\u00d5\u00da\7$\2\2\u00d6\u00d7\7\20\2"+
		"\2\u00d7\u00d8\5\32\16\2\u00d8\u00d9\7\21\2\2\u00d9\u00db\3\2\2\2\u00da"+
		"\u00d6\3\2\2\2\u00da\u00db\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00dd\7\22"+
		"\2\2\u00dd\u00df\7\23\2\2\u00de\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df"+
		"\31\3\2\2\2\u00e0\u00e5\5\30\r\2\u00e1\u00e2\7\13\2\2\u00e2\u00e4\5\30"+
		"\r\2\u00e3\u00e1\3\2\2\2\u00e4\u00e7\3\2\2\2\u00e5\u00e3\3\2\2\2\u00e5"+
		"\u00e6\3\2\2\2\u00e6\33\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e8\u00eb\5\36\20"+
		"\2\u00e9\u00eb\5 \21\2\u00ea\u00e8\3\2\2\2\u00ea\u00e9\3\2\2\2\u00eb\35"+
		"\3\2\2\2\u00ec\u00ed\7\24\2\2\u00ed\u00f0\7$\2\2\u00ee\u00ef\7\r\2\2\u00ef"+
		"\u00f1\5\30\r\2\u00f0\u00ee\3\2\2\2\u00f0\u00f1\3\2\2\2\u00f1\u00f4\3"+
		"\2\2\2\u00f2\u00f3\7\25\2\2\u00f3\u00f5\5\34\17\2\u00f4\u00f2\3\2\2\2"+
		"\u00f4\u00f5\3\2\2\2\u00f5\37\3\2\2\2\u00f6\u00f7\7\26\2\2\u00f7\u00fa"+
		"\7$\2\2\u00f8\u00f9\7\r\2\2\u00f9\u00fb\5\30\r\2\u00fa\u00f8\3\2\2\2\u00fa"+
		"\u00fb\3\2\2\2\u00fb\u00fe\3\2\2\2\u00fc\u00fd\7\25\2\2\u00fd\u00ff\5"+
		"\34\17\2\u00fe\u00fc\3\2\2\2\u00fe\u00ff\3\2\2\2\u00ff!\3\2\2\2\u0100"+
		"\u0101\7\27\2\2\u0101\u0105\7\4\2\2\u0102\u0104\5$\23\2\u0103\u0102\3"+
		"\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106"+
		"\u0108\3\2\2\2\u0107\u0105\3\2\2\2\u0108\u0109\7\6\2\2\u0109#\3\2\2\2"+
		"\u010a\u010d\5&\24\2\u010b\u010d\5\62\32\2\u010c\u010a\3\2\2\2\u010c\u010b"+
		"\3\2\2\2\u010d%\3\2\2\2\u010e\u010f\7\30\2\2\u010f\u0110\7$\2\2\u0110"+
		"\u0111\5(\25\2\u0111\'\3\2\2\2\u0112\u0114\7$\2\2\u0113\u0112\3\2\2\2"+
		"\u0113\u0114\3\2\2\2\u0114\u0120\3\2\2\2\u0115\u0116\7\16\2\2\u0116\u011b"+
		"\5*\26\2\u0117\u0118\7\13\2\2\u0118\u011a\5*\26\2\u0119\u0117\3\2\2\2"+
		"\u011a\u011d\3\2\2\2\u011b\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011e"+
		"\3\2\2\2\u011d\u011b\3\2\2\2\u011e\u011f\7\17\2\2\u011f\u0121\3\2\2\2"+
		"\u0120\u0115\3\2\2\2\u0120\u0121\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0126"+
		"\7\4\2\2\u0123\u0125\5*\26\2\u0124\u0123\3\2\2\2\u0125\u0128\3\2\2\2\u0126"+
		"\u0124\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0129\3\2\2\2\u0128\u0126\3\2"+
		"\2\2\u0129\u012a\7\6\2\2\u012a)\3\2\2\2\u012b\u012f\5\60\31\2\u012c\u012f"+
		"\5.\30\2\u012d\u012f\5,\27\2\u012e\u012b\3\2\2\2\u012e\u012c\3\2\2\2\u012e"+
		"\u012d\3\2\2\2\u012f+\3\2\2\2\u0130\u0132\7$\2\2\u0131\u0130\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\7\22\2\2\u0134\u0139\5"+
		"*\26\2\u0135\u0136\7\13\2\2\u0136\u0138\5*\26\2\u0137\u0135\3\2\2\2\u0138"+
		"\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2\2\2\u013a\u013c\3\2"+
		"\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7\23\2\2\u013d-\3\2\2\2\u013e\u0140"+
		"\7$\2\2\u013f\u013e\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\3\2\2\2\u0141"+
		"\u0142\5(\25\2\u0142/\3\2\2\2\u0143\u0145\7$\2\2\u0144\u0143\3\2\2\2\u0144"+
		"\u0145\3\2\2\2\u0145\u0146\3\2\2\2\u0146\u0147\7&\2\2\u0147\61\3\2\2\2"+
		"\u0148\u0149\7\31\2\2\u0149\u0155\7$\2\2\u014a\u014b\7\16\2\2\u014b\u0150"+
		"\5\64\33\2\u014c\u014d\7\13\2\2\u014d\u014f\5\64\33\2\u014e\u014c\3\2"+
		"\2\2\u014f\u0152\3\2\2\2\u0150\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151"+
		"\u0153\3\2\2\2\u0152\u0150\3\2\2\2\u0153\u0154\7\17\2\2\u0154\u0156\3"+
		"\2\2\2\u0155\u014a\3\2\2\2\u0155\u0156\3\2\2\2\u0156\u0157\3\2\2\2\u0157"+
		"\u015b\7\4\2\2\u0158\u015a\5\64\33\2\u0159\u0158\3\2\2\2\u015a\u015d\3"+
		"\2\2\2\u015b\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\u015e\3\2\2\2\u015d"+
		"\u015b\3\2\2\2\u015e\u015f\7\6\2\2\u015f\63\3\2\2\2\u0160\u0162\7$\2\2"+
		"\u0161\u0160\3\2\2\2\u0161\u0162\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0164"+
		"\5\66\34\2\u0164\65\3\2\2\2\u0165\u0169\58\35\2\u0166\u0169\5<\37\2\u0167"+
		"\u0169\5> \2\u0168\u0165\3\2\2\2\u0168\u0166\3\2\2\2\u0168\u0167\3\2\2"+
		"\2\u0169\67\3\2\2\2\u016a\u016f\7$\2\2\u016b\u016c\7\20\2\2\u016c\u016d"+
		"\5:\36\2\u016d\u016e\7\21\2\2\u016e\u0170\3\2\2\2\u016f\u016b\3\2\2\2"+
		"\u016f\u0170\3\2\2\2\u0170\u0173\3\2\2\2\u0171\u0172\7\22\2\2\u0172\u0174"+
		"\7\23\2\2\u0173\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u01749\3\2\2\2\u0175"+
		"\u017a\58\35\2\u0176\u0177\7\13\2\2\u0177\u0179\58\35\2\u0178\u0176\3"+
		"\2\2\2\u0179\u017c\3\2\2\2\u017a\u0178\3\2\2\2\u017a\u017b\3\2\2\2\u017b"+
		";\3\2\2\2\u017c\u017a\3\2\2\2\u017d\u017e\7\16\2\2\u017e\u0188\7\17\2"+
		"\2\u017f\u0180\7\32\2\2\u0180\u0185\58\35\2\u0181\u0182\7\13\2\2\u0182"+
		"\u0184\58\35\2\u0183\u0181\3\2\2\2\u0184\u0187\3\2\2\2\u0185\u0183\3\2"+
		"\2\2\u0185\u0186\3\2\2\2\u0186\u0189\3\2\2\2\u0187\u0185\3\2\2\2\u0188"+
		"\u017f\3\2\2\2\u0188\u0189\3\2\2\2\u0189=\3\2\2\2\u018a\u018b\7\32\2\2"+
		"\u018b\u018c\7$\2\2\u018c?\3\2\2\2\u018d\u018e\7\33\2\2\u018e\u0192\7"+
		"\4\2\2\u018f\u0191\5B\"\2\u0190\u018f\3\2\2\2\u0191\u0194\3\2\2\2\u0192"+
		"\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u0195\3\2\2\2\u0194\u0192\3\2"+
		"\2\2\u0195\u0196\7\6\2\2\u0196A\3\2\2\2\u0197\u0198\7\f\2\2\u0198\u0199"+
		"\7$\2\2\u0199\u019a\7\4\2\2\u019a\u019b\5D#\2\u019b\u019c\7\6\2\2\u019c"+
		"C\3\2\2\2\u019d\u019f\5F$\2\u019e\u019d\3\2\2\2\u019f\u01a2\3\2\2\2\u01a0"+
		"\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1E\3\2\2\2\u01a2\u01a0\3\2\2\2"+
		"\u01a3\u01a7\5H%\2\u01a4\u01a7\5P)\2\u01a5\u01a7\5R*\2\u01a6\u01a3\3\2"+
		"\2\2\u01a6\u01a4\3\2\2\2\u01a6\u01a5\3\2\2\2\u01a7G\3\2\2\2\u01a8\u01a9"+
		"\7\34\2\2\u01a9\u01aa\7\16\2\2\u01aa\u01ab\5L\'\2\u01ab\u01ac\7\17\2\2"+
		"\u01ac\u01ad\7\4\2\2\u01ad\u01ae\5D#\2\u01ae\u01b0\7\6\2\2\u01af\u01b1"+
		"\5J&\2\u01b0\u01af\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1I\3\2\2\2\u01b2\u01b3"+
		"\7\35\2\2\u01b3\u01b4\7\4\2\2\u01b4\u01b5\5D#\2\u01b5\u01b6\7\6\2\2\u01b6"+
		"K\3\2\2\2\u01b7\u01bb\5R*\2\u01b8\u01b9\5N(\2\u01b9\u01ba\5R*\2\u01ba"+
		"\u01bc\3\2\2\2\u01bb\u01b8\3\2\2\2\u01bb\u01bc\3\2\2\2\u01bcM\3\2\2\2"+
		"\u01bd\u01c4\7\36\2\2\u01be\u01c4\7\37\2\2\u01bf\u01c4\7\20\2\2\u01c0"+
		"\u01c4\7\21\2\2\u01c1\u01c2\7 \2\2\u01c2\u01c4\7!\2\2\u01c3\u01bd\3\2"+
		"\2\2\u01c3\u01be\3\2\2\2\u01c3\u01bf\3\2\2\2\u01c3\u01c0\3\2\2\2\u01c3"+
		"\u01c1\3\2\2\2\u01c4O\3\2\2\2\u01c5\u01c6\7\n\2\2\u01c6\u01c7\7\16\2\2"+
		"\u01c7\u01c8\7$\2\2\u01c8\u01c9\7\"\2\2\u01c9\u01ca\7$\2\2\u01ca\u01cb"+
		"\7\17\2\2\u01cb\u01cc\7\4\2\2\u01cc\u01cd\5D#\2\u01cd\u01ce\7\6\2\2\u01ce"+
		"Q\3\2\2\2\u01cf\u01d3\5T+\2\u01d0\u01d3\5V,\2\u01d1\u01d3\5X-\2\u01d2"+
		"\u01cf\3\2\2\2\u01d2\u01d0\3\2\2\2\u01d2\u01d1\3\2\2\2\u01d3S\3\2\2\2"+
		"\u01d4\u01d5\7$\2\2\u01d5\u01d6\7\22\2\2\u01d6\u01d7\7$\2\2\u01d7\u01d8"+
		"\7\23\2\2\u01d8U\3\2\2\2\u01d9\u01da\7$\2\2\u01da\u01db\7\16\2\2\u01db"+
		"\u01dc\7\17\2\2\u01dcW\3\2\2\2\u01dd\u01de\7$\2\2\u01deY\3\2\2\29]ckp"+
		"v|\u0090\u0093\u0099\u00a0\u00a7\u00b0\u00b3\u00b9\u00bd\u00c8\u00cb\u00ce"+
		"\u00d3\u00da\u00de\u00e5\u00ea\u00f0\u00f4\u00fa\u00fe\u0105\u010c\u0113"+
		"\u011b\u0120\u0126\u012e\u0131\u0139\u013f\u0144\u0150\u0155\u015b\u0161"+
		"\u0168\u016f\u0173\u017a\u0185\u0188\u0192\u01a0\u01a6\u01b0\u01bb\u01c3"+
		"\u01d2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}