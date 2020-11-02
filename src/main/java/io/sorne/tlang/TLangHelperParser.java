// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangHelper.g4 by ANTLR 4.8
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
public class TLangHelperParser extends Parser {
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
		RULE_helperBlock = 0, RULE_helperFunc = 1, RULE_helperCurrying = 2, RULE_helperParam = 3, 
		RULE_helperParamType = 4, RULE_helperObjType = 5, RULE_helperArrayType = 6, 
		RULE_helperFuncType = 7, RULE_helperContent = 8, RULE_helperStatement = 9, 
		RULE_helperIf = 10, RULE_helperElse = 11, RULE_helperConditionBlock = 12, 
		RULE_helperCondition = 13, RULE_conditionMark = 14, RULE_helperFor = 15, 
		RULE_helperCallObj = 16, RULE_helperCallObjType = 17, RULE_helperCallString = 18, 
		RULE_helperCallNumber = 19, RULE_helperCallText = 20, RULE_helperCallArray = 21, 
		RULE_helperCallFunc = 22, RULE_helperCallVariable = 23;
	private static String[] makeRuleNames() {
		return new String[] {
			"helperBlock", "helperFunc", "helperCurrying", "helperParam", "helperParamType", 
			"helperObjType", "helperArrayType", "helperFuncType", "helperContent", 
			"helperStatement", "helperIf", "helperElse", "helperConditionBlock", 
			"helperCondition", "conditionMark", "helperFor", "helperCallObj", "helperCallObjType", 
			"helperCallString", "helperCallNumber", "helperCallText", "helperCallArray", 
			"helperCallFunc", "helperCallVariable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'helper'", "'{'", "'}'", "'func'", "'('", "')'", "':'", "','", 
			"'['", "']'", "'if'", "'else'", "'&&'", "'||'", "'=='", "'!='", "'<'", 
			"'>'", "'<='", "'>='", "'for'", "'in'", "'to'", "'until'", "'.'", "'_'"
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
	public String getGrammarFileName() { return "TLangHelper.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TLangHelperParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperBlockContext helperBlock() throws RecognitionException {
		HelperBlockContext _localctx = new HelperBlockContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_helperBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(T__0);
			setState(49);
			match(T__1);
			setState(53);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(50);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(55);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(56);
			match(T__2);
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
		public HelperCurryingContext helperCurrying;
		public List<HelperCurryingContext> currying = new ArrayList<HelperCurryingContext>();
		public HelperParamTypeContext helperParamType;
		public List<HelperParamTypeContext> retVals = new ArrayList<HelperParamTypeContext>();
		public HelperContentContext body;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
		}
		public List<HelperParamTypeContext> helperParamType() {
			return getRuleContexts(HelperParamTypeContext.class);
		}
		public HelperParamTypeContext helperParamType(int i) {
			return getRuleContext(HelperParamTypeContext.class,i);
		}
		public List<HelperCurryingContext> helperCurrying() {
			return getRuleContexts(HelperCurryingContext.class);
		}
		public HelperCurryingContext helperCurrying(int i) {
			return getRuleContext(HelperCurryingContext.class,i);
		}
		public HelperFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperFuncContext helperFunc() throws RecognitionException {
		HelperFuncContext _localctx = new HelperFuncContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_helperFunc);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(T__3);
			setState(59);
			((HelperFuncContext)_localctx).name = match(ID);
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(60);
				match(T__4);
				setState(62);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__4 || _la==ID) {
					{
					setState(61);
					((HelperFuncContext)_localctx).helperCurrying = helperCurrying();
					((HelperFuncContext)_localctx).currying.add(((HelperFuncContext)_localctx).helperCurrying);
					}
				}

				setState(64);
				match(T__5);
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(70);
				match(T__6);
				setState(71);
				((HelperFuncContext)_localctx).helperParamType = helperParamType();
				((HelperFuncContext)_localctx).retVals.add(((HelperFuncContext)_localctx).helperParamType);
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__7) {
					{
					{
					setState(72);
					match(T__7);
					setState(73);
					((HelperFuncContext)_localctx).helperParamType = helperParamType();
					((HelperFuncContext)_localctx).retVals.add(((HelperFuncContext)_localctx).helperParamType);
					}
					}
					setState(78);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(81);
			match(T__1);
			setState(82);
			((HelperFuncContext)_localctx).body = helperContent();
			setState(83);
			match(T__2);
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

	public static class HelperCurryingContext extends ParserRuleContext {
		public HelperParamContext helperParam;
		public List<HelperParamContext> params = new ArrayList<HelperParamContext>();
		public List<HelperParamContext> helperParam() {
			return getRuleContexts(HelperParamContext.class);
		}
		public HelperParamContext helperParam(int i) {
			return getRuleContext(HelperParamContext.class,i);
		}
		public HelperCurryingContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCurrying; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCurrying(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCurrying(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCurrying(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCurryingContext helperCurrying() throws RecognitionException {
		HelperCurryingContext _localctx = new HelperCurryingContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_helperCurrying);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			((HelperCurryingContext)_localctx).helperParam = helperParam();
			((HelperCurryingContext)_localctx).params.add(((HelperCurryingContext)_localctx).helperParam);
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(86);
				match(T__7);
				setState(87);
				((HelperCurryingContext)_localctx).helperParam = helperParam();
				((HelperCurryingContext)_localctx).params.add(((HelperCurryingContext)_localctx).helperParam);
				}
				}
				setState(92);
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

	public static class HelperParamContext extends ParserRuleContext {
		public Token param;
		public HelperParamTypeContext type;
		public HelperParamTypeContext helperParamType() {
			return getRuleContext(HelperParamTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperParam(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperParamContext helperParam() throws RecognitionException {
		HelperParamContext _localctx = new HelperParamContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_helperParam);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(94);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(93);
				((HelperParamContext)_localctx).param = match(ID);
				}
				break;
			}
			setState(96);
			((HelperParamContext)_localctx).type = helperParamType();
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

	public static class HelperParamTypeContext extends ParserRuleContext {
		public HelperObjTypeContext helperObjType() {
			return getRuleContext(HelperObjTypeContext.class,0);
		}
		public HelperArrayTypeContext helperArrayType() {
			return getRuleContext(HelperArrayTypeContext.class,0);
		}
		public HelperFuncTypeContext helperFuncType() {
			return getRuleContext(HelperFuncTypeContext.class,0);
		}
		public HelperParamTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperParamType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperParamType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperParamType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperParamType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperParamTypeContext helperParamType() throws RecognitionException {
		HelperParamTypeContext _localctx = new HelperParamTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_helperParamType);
		try {
			setState(101);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				helperObjType();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(99);
				helperArrayType();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(100);
				helperFuncType();
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

	public static class HelperObjTypeContext extends ParserRuleContext {
		public Token tpye;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperObjTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperObjType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperObjType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperObjType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperObjType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperObjTypeContext helperObjType() throws RecognitionException {
		HelperObjTypeContext _localctx = new HelperObjTypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_helperObjType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(103);
			((HelperObjTypeContext)_localctx).tpye = match(ID);
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

	public static class HelperArrayTypeContext extends ParserRuleContext {
		public Token tpye;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperArrayTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperArrayType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperArrayType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperArrayType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperArrayType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperArrayTypeContext helperArrayType() throws RecognitionException {
		HelperArrayTypeContext _localctx = new HelperArrayTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_helperArrayType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(105);
			((HelperArrayTypeContext)_localctx).tpye = match(ID);
			setState(106);
			match(T__8);
			setState(107);
			match(T__9);
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

	public static class HelperFuncTypeContext extends ParserRuleContext {
		public Token type;
		public HelperCurryingContext helperCurrying;
		public List<HelperCurryingContext> currying = new ArrayList<HelperCurryingContext>();
		public HelperParamTypeContext helperParamType;
		public List<HelperParamTypeContext> retVals = new ArrayList<HelperParamTypeContext>();
		public List<HelperParamTypeContext> helperParamType() {
			return getRuleContexts(HelperParamTypeContext.class);
		}
		public HelperParamTypeContext helperParamType(int i) {
			return getRuleContext(HelperParamTypeContext.class,i);
		}
		public List<HelperCurryingContext> helperCurrying() {
			return getRuleContexts(HelperCurryingContext.class);
		}
		public HelperCurryingContext helperCurrying(int i) {
			return getRuleContext(HelperCurryingContext.class,i);
		}
		public HelperFuncTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperFuncType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperFuncType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperFuncType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperFuncType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperFuncTypeContext helperFuncType() throws RecognitionException {
		HelperFuncTypeContext _localctx = new HelperFuncTypeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_helperFuncType);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			((HelperFuncTypeContext)_localctx).type = match(T__4);
			setState(111);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4 || _la==ID) {
				{
				setState(110);
				((HelperFuncTypeContext)_localctx).helperCurrying = helperCurrying();
				((HelperFuncTypeContext)_localctx).currying.add(((HelperFuncTypeContext)_localctx).helperCurrying);
				}
			}

			setState(113);
			match(T__5);
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(114);
				match(T__4);
				setState(115);
				((HelperFuncTypeContext)_localctx).helperCurrying = helperCurrying();
				((HelperFuncTypeContext)_localctx).currying.add(((HelperFuncTypeContext)_localctx).helperCurrying);
				setState(116);
				match(T__5);
				}
				}
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			match(T__6);
			setState(124);
			((HelperFuncTypeContext)_localctx).helperParamType = helperParamType();
			((HelperFuncTypeContext)_localctx).retVals.add(((HelperFuncTypeContext)_localctx).helperParamType);
			setState(129);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(125);
					match(T__7);
					setState(126);
					((HelperFuncTypeContext)_localctx).helperParamType = helperParamType();
					((HelperFuncTypeContext)_localctx).retVals.add(((HelperFuncTypeContext)_localctx).helperParamType);
					}
					} 
				}
				setState(131);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperContentContext helperContent() throws RecognitionException {
		HelperContentContext _localctx = new HelperContentContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_helperContent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__8) | (1L << T__10) | (1L << T__20) | (1L << T__25) | (1L << TEXT) | (1L << ID) | (1L << STRING) | (1L << NUMBER))) != 0)) {
				{
				{
				setState(132);
				((HelperContentContext)_localctx).helperStatement = helperStatement();
				((HelperContentContext)_localctx).content.add(((HelperContentContext)_localctx).helperStatement);
				}
				}
				setState(137);
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
		public HelperConditionBlockContext helperConditionBlock() {
			return getRuleContext(HelperConditionBlockContext.class,0);
		}
		public HelperStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperStatementContext helperStatement() throws RecognitionException {
		HelperStatementContext _localctx = new HelperStatementContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_helperStatement);
		try {
			setState(142);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				helperIf();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				helperFor();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
				helperCallObj();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(141);
				helperConditionBlock();
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

	public static class HelperIfContext extends ParserRuleContext {
		public HelperConditionBlockContext condition;
		public HelperContentContext body;
		public HelperElseContext orElse;
		public HelperConditionBlockContext helperConditionBlock() {
			return getRuleContext(HelperConditionBlockContext.class,0);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperIf(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperIf(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperIf(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperIfContext helperIf() throws RecognitionException {
		HelperIfContext _localctx = new HelperIfContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_helperIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			match(T__10);
			setState(145);
			match(T__4);
			setState(146);
			((HelperIfContext)_localctx).condition = helperConditionBlock();
			setState(147);
			match(T__5);
			setState(148);
			match(T__1);
			setState(149);
			((HelperIfContext)_localctx).body = helperContent();
			setState(150);
			match(T__2);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__11) {
				{
				setState(151);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperElse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperElse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperElse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperElseContext helperElse() throws RecognitionException {
		HelperElseContext _localctx = new HelperElseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_helperElse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(T__11);
			setState(155);
			match(T__1);
			setState(156);
			((HelperElseContext)_localctx).body = helperContent();
			setState(157);
			match(T__2);
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

	public static class HelperConditionBlockContext extends ParserRuleContext {
		public HelperConditionContext helperCondition;
		public List<HelperConditionContext> content = new ArrayList<HelperConditionContext>();
		public Token link;
		public HelperConditionContext helperCondition() {
			return getRuleContext(HelperConditionContext.class,0);
		}
		public List<HelperConditionBlockContext> helperConditionBlock() {
			return getRuleContexts(HelperConditionBlockContext.class);
		}
		public HelperConditionBlockContext helperConditionBlock(int i) {
			return getRuleContext(HelperConditionBlockContext.class,i);
		}
		public HelperConditionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperConditionBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperConditionBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperConditionBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperConditionBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperConditionBlockContext helperConditionBlock() throws RecognitionException {
		HelperConditionBlockContext _localctx = new HelperConditionBlockContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_helperConditionBlock);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(159);
				match(T__4);
				}
			}

			setState(162);
			((HelperConditionBlockContext)_localctx).helperCondition = helperCondition();
			((HelperConditionBlockContext)_localctx).content.add(((HelperConditionBlockContext)_localctx).helperCondition);
			setState(164);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(163);
				match(T__5);
				}
				break;
			}
			setState(170);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(166);
					((HelperConditionBlockContext)_localctx).link = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__12 || _la==T__13) ) {
						((HelperConditionBlockContext)_localctx).link = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(167);
					helperConditionBlock();
					}
					} 
				}
				setState(172);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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

	public static class HelperConditionContext extends ParserRuleContext {
		public HelperCallObjContext arg1;
		public ConditionMarkContext mark;
		public HelperCallObjContext arg2;
		public Token link;
		public List<HelperCallObjContext> helperCallObj() {
			return getRuleContexts(HelperCallObjContext.class);
		}
		public HelperCallObjContext helperCallObj(int i) {
			return getRuleContext(HelperCallObjContext.class,i);
		}
		public List<HelperConditionContext> helperCondition() {
			return getRuleContexts(HelperConditionContext.class);
		}
		public HelperConditionContext helperCondition(int i) {
			return getRuleContext(HelperConditionContext.class,i);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperConditionContext helperCondition() throws RecognitionException {
		HelperConditionContext _localctx = new HelperConditionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_helperCondition);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
			((HelperConditionContext)_localctx).arg1 = helperCallObj();
			setState(177);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18))) != 0)) {
				{
				setState(174);
				((HelperConditionContext)_localctx).mark = conditionMark();
				setState(175);
				((HelperConditionContext)_localctx).arg2 = helperCallObj();
				}
			}

			setState(183);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(179);
					((HelperConditionContext)_localctx).link = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==T__12 || _la==T__13) ) {
						((HelperConditionContext)_localctx).link = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(180);
					helperCondition();
					}
					} 
				}
				setState(185);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterConditionMark(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitConditionMark(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitConditionMark(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionMarkContext conditionMark() throws RecognitionException {
		ConditionMarkContext _localctx = new ConditionMarkContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_conditionMark);
		try {
			setState(192);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__14:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				match(T__14);
				}
				break;
			case T__15:
				enterOuterAlt(_localctx, 2);
				{
				setState(187);
				match(T__15);
				}
				break;
			case T__16:
				enterOuterAlt(_localctx, 3);
				{
				setState(188);
				match(T__16);
				}
				break;
			case T__17:
				enterOuterAlt(_localctx, 4);
				{
				setState(189);
				match(T__17);
				}
				break;
			case T__18:
				enterOuterAlt(_localctx, 5);
				{
				setState(190);
				match(T__18);
				setState(191);
				match(T__19);
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
		public Token type;
		public HelperCallObjContext array;
		public HelperContentContext body;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperCallObjContext helperCallObj() {
			return getRuleContext(HelperCallObjContext.class,0);
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
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperFor(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperFor(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperFor(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperForContext helperFor() throws RecognitionException {
		HelperForContext _localctx = new HelperForContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_helperFor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194);
			match(T__20);
			setState(195);
			match(T__4);
			setState(196);
			((HelperForContext)_localctx).var = match(ID);
			setState(197);
			((HelperForContext)_localctx).type = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__21) | (1L << T__22) | (1L << T__23))) != 0)) ) {
				((HelperForContext)_localctx).type = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(198);
			((HelperForContext)_localctx).array = helperCallObj();
			setState(199);
			match(T__5);
			setState(200);
			match(T__1);
			setState(201);
			((HelperForContext)_localctx).body = helperContent();
			setState(202);
			match(T__2);
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
		public HelperCallObjTypeContext helperCallObjType;
		public List<HelperCallObjTypeContext> objs = new ArrayList<HelperCallObjTypeContext>();
		public List<HelperCallObjTypeContext> helperCallObjType() {
			return getRuleContexts(HelperCallObjTypeContext.class);
		}
		public HelperCallObjTypeContext helperCallObjType(int i) {
			return getRuleContext(HelperCallObjTypeContext.class,i);
		}
		public HelperCallObjContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallObj; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallObj(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallObj(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallObj(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallObjContext helperCallObj() throws RecognitionException {
		HelperCallObjContext _localctx = new HelperCallObjContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_helperCallObj);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204);
			((HelperCallObjContext)_localctx).helperCallObjType = helperCallObjType();
			((HelperCallObjContext)_localctx).objs.add(((HelperCallObjContext)_localctx).helperCallObjType);
			setState(209);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__24) {
				{
				{
				setState(205);
				match(T__24);
				setState(206);
				((HelperCallObjContext)_localctx).helperCallObjType = helperCallObjType();
				((HelperCallObjContext)_localctx).objs.add(((HelperCallObjContext)_localctx).helperCallObjType);
				}
				}
				setState(211);
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

	public static class HelperCallObjTypeContext extends ParserRuleContext {
		public HelperCallArrayContext helperCallArray() {
			return getRuleContext(HelperCallArrayContext.class,0);
		}
		public HelperCallStringContext helperCallString() {
			return getRuleContext(HelperCallStringContext.class,0);
		}
		public HelperCallTextContext helperCallText() {
			return getRuleContext(HelperCallTextContext.class,0);
		}
		public HelperCallNumberContext helperCallNumber() {
			return getRuleContext(HelperCallNumberContext.class,0);
		}
		public HelperCallFuncContext helperCallFunc() {
			return getRuleContext(HelperCallFuncContext.class,0);
		}
		public HelperCallVariableContext helperCallVariable() {
			return getRuleContext(HelperCallVariableContext.class,0);
		}
		public HelperCallObjTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallObjType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallObjType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallObjType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallObjType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallObjTypeContext helperCallObjType() throws RecognitionException {
		HelperCallObjTypeContext _localctx = new HelperCallObjTypeContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_helperCallObjType);
		try {
			setState(218);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(212);
				helperCallArray();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(213);
				helperCallString();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(214);
				helperCallText();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(215);
				helperCallNumber();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(216);
				helperCallFunc();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(217);
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

	public static class HelperCallStringContext extends ParserRuleContext {
		public Token type;
		public TerminalNode STRING() { return getToken(TLangHelperParser.STRING, 0); }
		public HelperCallStringContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallString; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallString(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallString(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallStringContext helperCallString() throws RecognitionException {
		HelperCallStringContext _localctx = new HelperCallStringContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_helperCallString);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			((HelperCallStringContext)_localctx).type = match(STRING);
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

	public static class HelperCallNumberContext extends ParserRuleContext {
		public Token type;
		public TerminalNode NUMBER() { return getToken(TLangHelperParser.NUMBER, 0); }
		public HelperCallNumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallNumber; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallNumber(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallNumber(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallNumberContext helperCallNumber() throws RecognitionException {
		HelperCallNumberContext _localctx = new HelperCallNumberContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_helperCallNumber);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(222);
			((HelperCallNumberContext)_localctx).type = match(NUMBER);
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

	public static class HelperCallTextContext extends ParserRuleContext {
		public Token type;
		public TerminalNode TEXT() { return getToken(TLangHelperParser.TEXT, 0); }
		public HelperCallTextContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallText; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallText(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallText(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallText(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallTextContext helperCallText() throws RecognitionException {
		HelperCallTextContext _localctx = new HelperCallTextContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_helperCallText);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			((HelperCallTextContext)_localctx).type = match(TEXT);
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
		public HelperCallObjContext elem;
		public HelperCallObjContext helperCallObj() {
			return getRuleContext(HelperCallObjContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperCallArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallArray; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallArray(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallArray(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallArrayContext helperCallArray() throws RecognitionException {
		HelperCallArrayContext _localctx = new HelperCallArrayContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_helperCallArray);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(227);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(226);
				((HelperCallArrayContext)_localctx).name = match(ID);
				}
			}

			setState(229);
			match(T__8);
			setState(230);
			((HelperCallArrayContext)_localctx).elem = helperCallObj();
			setState(231);
			match(T__9);
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
		public Token s5;
		public List<Token> currying = new ArrayList<Token>();
		public HelperCallObjContext helperCallObj;
		public List<HelperCallObjContext> params = new ArrayList<HelperCallObjContext>();
		public List<HelperCallObjContext> helperCallObj() {
			return getRuleContexts(HelperCallObjContext.class);
		}
		public HelperCallObjContext helperCallObj(int i) {
			return getRuleContext(HelperCallObjContext.class,i);
		}
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperCallFuncContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallFunc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallFunc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallFunc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallFunc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallFuncContext helperCallFunc() throws RecognitionException {
		HelperCallFuncContext _localctx = new HelperCallFuncContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_helperCallFunc);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				{
				{
				setState(233);
				((HelperCallFuncContext)_localctx).name = match(ID);
				}
				}
				break;
			case T__25:
				{
				setState(234);
				match(T__25);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(237);
			((HelperCallFuncContext)_localctx).s5 = match(T__4);
			((HelperCallFuncContext)_localctx).currying.add(((HelperCallFuncContext)_localctx).s5);
			setState(238);
			((HelperCallFuncContext)_localctx).helperCallObj = helperCallObj();
			((HelperCallFuncContext)_localctx).params.add(((HelperCallFuncContext)_localctx).helperCallObj);
			setState(243);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(239);
				match(T__7);
				setState(240);
				((HelperCallFuncContext)_localctx).helperCallObj = helperCallObj();
				((HelperCallFuncContext)_localctx).params.add(((HelperCallFuncContext)_localctx).helperCallObj);
				}
				}
				setState(245);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(246);
			match(T__5);
			setState(260);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(247);
					((HelperCallFuncContext)_localctx).s5 = match(T__4);
					((HelperCallFuncContext)_localctx).currying.add(((HelperCallFuncContext)_localctx).s5);
					setState(248);
					((HelperCallFuncContext)_localctx).helperCallObj = helperCallObj();
					((HelperCallFuncContext)_localctx).params.add(((HelperCallFuncContext)_localctx).helperCallObj);
					setState(253);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==T__7) {
						{
						{
						setState(249);
						match(T__7);
						setState(250);
						((HelperCallFuncContext)_localctx).helperCallObj = helperCallObj();
						((HelperCallFuncContext)_localctx).params.add(((HelperCallFuncContext)_localctx).helperCallObj);
						}
						}
						setState(255);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					setState(256);
					match(T__5);
					}
					} 
				}
				setState(262);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
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

	public static class HelperCallVariableContext extends ParserRuleContext {
		public Token name;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperCallVariableContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_helperCallVariable; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).enterHelperCallVariable(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangHelperListener ) ((TLangHelperListener)listener).exitHelperCallVariable(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangHelperVisitor ) return ((TLangHelperVisitor<? extends T>)visitor).visitHelperCallVariable(this);
			else return visitor.visitChildren(this);
		}
	}

	public final HelperCallVariableContext helperCallVariable() throws RecognitionException {
		HelperCallVariableContext _localctx = new HelperCallVariableContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_helperCallVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(263);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3!\u010c\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\3\2\3\2\3\2\7\2\66\n\2\f\2\16\29\13\2\3\2\3\2\3\3\3\3\3\3\3\3\5\3A\n"+
		"\3\3\3\7\3D\n\3\f\3\16\3G\13\3\3\3\3\3\3\3\3\3\7\3M\n\3\f\3\16\3P\13\3"+
		"\5\3R\n\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\7\4[\n\4\f\4\16\4^\13\4\3\5\5\5"+
		"a\n\5\3\5\3\5\3\6\3\6\3\6\5\6h\n\6\3\7\3\7\3\b\3\b\3\b\3\b\3\t\3\t\5\t"+
		"r\n\t\3\t\3\t\3\t\3\t\3\t\7\ty\n\t\f\t\16\t|\13\t\3\t\3\t\3\t\3\t\7\t"+
		"\u0082\n\t\f\t\16\t\u0085\13\t\3\n\7\n\u0088\n\n\f\n\16\n\u008b\13\n\3"+
		"\13\3\13\3\13\3\13\5\13\u0091\n\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5\f"+
		"\u009b\n\f\3\r\3\r\3\r\3\r\3\r\3\16\5\16\u00a3\n\16\3\16\3\16\5\16\u00a7"+
		"\n\16\3\16\3\16\7\16\u00ab\n\16\f\16\16\16\u00ae\13\16\3\17\3\17\3\17"+
		"\3\17\5\17\u00b4\n\17\3\17\3\17\7\17\u00b8\n\17\f\17\16\17\u00bb\13\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00c3\n\20\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\7\22\u00d2\n\22\f\22\16\22\u00d5"+
		"\13\22\3\23\3\23\3\23\3\23\3\23\3\23\5\23\u00dd\n\23\3\24\3\24\3\25\3"+
		"\25\3\26\3\26\3\27\5\27\u00e6\n\27\3\27\3\27\3\27\3\27\3\30\3\30\5\30"+
		"\u00ee\n\30\3\30\3\30\3\30\3\30\7\30\u00f4\n\30\f\30\16\30\u00f7\13\30"+
		"\3\30\3\30\3\30\3\30\3\30\7\30\u00fe\n\30\f\30\16\30\u0101\13\30\3\30"+
		"\3\30\7\30\u0105\n\30\f\30\16\30\u0108\13\30\3\31\3\31\3\31\2\2\32\2\4"+
		"\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\2\4\3\2\17\20\3\2\30\32"+
		"\2\u0118\2\62\3\2\2\2\4<\3\2\2\2\6W\3\2\2\2\b`\3\2\2\2\ng\3\2\2\2\fi\3"+
		"\2\2\2\16k\3\2\2\2\20o\3\2\2\2\22\u0089\3\2\2\2\24\u0090\3\2\2\2\26\u0092"+
		"\3\2\2\2\30\u009c\3\2\2\2\32\u00a2\3\2\2\2\34\u00af\3\2\2\2\36\u00c2\3"+
		"\2\2\2 \u00c4\3\2\2\2\"\u00ce\3\2\2\2$\u00dc\3\2\2\2&\u00de\3\2\2\2(\u00e0"+
		"\3\2\2\2*\u00e2\3\2\2\2,\u00e5\3\2\2\2.\u00ed\3\2\2\2\60\u0109\3\2\2\2"+
		"\62\63\7\3\2\2\63\67\7\4\2\2\64\66\5\4\3\2\65\64\3\2\2\2\669\3\2\2\2\67"+
		"\65\3\2\2\2\678\3\2\2\28:\3\2\2\29\67\3\2\2\2:;\7\5\2\2;\3\3\2\2\2<=\7"+
		"\6\2\2=E\7\36\2\2>@\7\7\2\2?A\5\6\4\2@?\3\2\2\2@A\3\2\2\2AB\3\2\2\2BD"+
		"\7\b\2\2C>\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FQ\3\2\2\2GE\3\2\2\2H"+
		"I\7\t\2\2IN\5\n\6\2JK\7\n\2\2KM\5\n\6\2LJ\3\2\2\2MP\3\2\2\2NL\3\2\2\2"+
		"NO\3\2\2\2OR\3\2\2\2PN\3\2\2\2QH\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\7\4\2\2"+
		"TU\5\22\n\2UV\7\5\2\2V\5\3\2\2\2W\\\5\b\5\2XY\7\n\2\2Y[\5\b\5\2ZX\3\2"+
		"\2\2[^\3\2\2\2\\Z\3\2\2\2\\]\3\2\2\2]\7\3\2\2\2^\\\3\2\2\2_a\7\36\2\2"+
		"`_\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\5\n\6\2c\t\3\2\2\2dh\5\f\7\2eh\5\16\b"+
		"\2fh\5\20\t\2gd\3\2\2\2ge\3\2\2\2gf\3\2\2\2h\13\3\2\2\2ij\7\36\2\2j\r"+
		"\3\2\2\2kl\7\36\2\2lm\7\13\2\2mn\7\f\2\2n\17\3\2\2\2oq\7\7\2\2pr\5\6\4"+
		"\2qp\3\2\2\2qr\3\2\2\2rs\3\2\2\2sz\7\b\2\2tu\7\7\2\2uv\5\6\4\2vw\7\b\2"+
		"\2wy\3\2\2\2xt\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|z\3\2\2"+
		"\2}~\7\t\2\2~\u0083\5\n\6\2\177\u0080\7\n\2\2\u0080\u0082\5\n\6\2\u0081"+
		"\177\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2"+
		"\2\u0084\21\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0088\5\24\13\2\u0087\u0086"+
		"\3\2\2\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\23\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u0091\5\26\f\2\u008d\u0091\5 \21"+
		"\2\u008e\u0091\5\"\22\2\u008f\u0091\5\32\16\2\u0090\u008c\3\2\2\2\u0090"+
		"\u008d\3\2\2\2\u0090\u008e\3\2\2\2\u0090\u008f\3\2\2\2\u0091\25\3\2\2"+
		"\2\u0092\u0093\7\r\2\2\u0093\u0094\7\7\2\2\u0094\u0095\5\32\16\2\u0095"+
		"\u0096\7\b\2\2\u0096\u0097\7\4\2\2\u0097\u0098\5\22\n\2\u0098\u009a\7"+
		"\5\2\2\u0099\u009b\5\30\r\2\u009a\u0099\3\2\2\2\u009a\u009b\3\2\2\2\u009b"+
		"\27\3\2\2\2\u009c\u009d\7\16\2\2\u009d\u009e\7\4\2\2\u009e\u009f\5\22"+
		"\n\2\u009f\u00a0\7\5\2\2\u00a0\31\3\2\2\2\u00a1\u00a3\7\7\2\2\u00a2\u00a1"+
		"\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\5\34\17\2"+
		"\u00a5\u00a7\7\b\2\2\u00a6\u00a5\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00ac"+
		"\3\2\2\2\u00a8\u00a9\t\2\2\2\u00a9\u00ab\5\32\16\2\u00aa\u00a8\3\2\2\2"+
		"\u00ab\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\33"+
		"\3\2\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b3\5\"\22\2\u00b0\u00b1\5\36\20"+
		"\2\u00b1\u00b2\5\"\22\2\u00b2\u00b4\3\2\2\2\u00b3\u00b0\3\2\2\2\u00b3"+
		"\u00b4\3\2\2\2\u00b4\u00b9\3\2\2\2\u00b5\u00b6\t\2\2\2\u00b6\u00b8\5\34"+
		"\17\2\u00b7\u00b5\3\2\2\2\u00b8\u00bb\3\2\2\2\u00b9\u00b7\3\2\2\2\u00b9"+
		"\u00ba\3\2\2\2\u00ba\35\3\2\2\2\u00bb\u00b9\3\2\2\2\u00bc\u00c3\7\21\2"+
		"\2\u00bd\u00c3\7\22\2\2\u00be\u00c3\7\23\2\2\u00bf\u00c3\7\24\2\2\u00c0"+
		"\u00c1\7\25\2\2\u00c1\u00c3\7\26\2\2\u00c2\u00bc\3\2\2\2\u00c2\u00bd\3"+
		"\2\2\2\u00c2\u00be\3\2\2\2\u00c2\u00bf\3\2\2\2\u00c2\u00c0\3\2\2\2\u00c3"+
		"\37\3\2\2\2\u00c4\u00c5\7\27\2\2\u00c5\u00c6\7\7\2\2\u00c6\u00c7\7\36"+
		"\2\2\u00c7\u00c8\t\3\2\2\u00c8\u00c9\5\"\22\2\u00c9\u00ca\7\b\2\2\u00ca"+
		"\u00cb\7\4\2\2\u00cb\u00cc\5\22\n\2\u00cc\u00cd\7\5\2\2\u00cd!\3\2\2\2"+
		"\u00ce\u00d3\5$\23\2\u00cf\u00d0\7\33\2\2\u00d0\u00d2\5$\23\2\u00d1\u00cf"+
		"\3\2\2\2\u00d2\u00d5\3\2\2\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4"+
		"#\3\2\2\2\u00d5\u00d3\3\2\2\2\u00d6\u00dd\5,\27\2\u00d7\u00dd\5&\24\2"+
		"\u00d8\u00dd\5*\26\2\u00d9\u00dd\5(\25\2\u00da\u00dd\5.\30\2\u00db\u00dd"+
		"\5\60\31\2\u00dc\u00d6\3\2\2\2\u00dc\u00d7\3\2\2\2\u00dc\u00d8\3\2\2\2"+
		"\u00dc\u00d9\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc\u00db\3\2\2\2\u00dd%\3"+
		"\2\2\2\u00de\u00df\7 \2\2\u00df\'\3\2\2\2\u00e0\u00e1\7!\2\2\u00e1)\3"+
		"\2\2\2\u00e2\u00e3\7\35\2\2\u00e3+\3\2\2\2\u00e4\u00e6\7\36\2\2\u00e5"+
		"\u00e4\3\2\2\2\u00e5\u00e6\3\2\2\2\u00e6\u00e7\3\2\2\2\u00e7\u00e8\7\13"+
		"\2\2\u00e8\u00e9\5\"\22\2\u00e9\u00ea\7\f\2\2\u00ea-\3\2\2\2\u00eb\u00ee"+
		"\7\36\2\2\u00ec\u00ee\7\34\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ec\3\2\2\2"+
		"\u00ee\u00ef\3\2\2\2\u00ef\u00f0\7\7\2\2\u00f0\u00f5\5\"\22\2\u00f1\u00f2"+
		"\7\n\2\2\u00f2\u00f4\5\"\22\2\u00f3\u00f1\3\2\2\2\u00f4\u00f7\3\2\2\2"+
		"\u00f5\u00f3\3\2\2\2\u00f5\u00f6\3\2\2\2\u00f6\u00f8\3\2\2\2\u00f7\u00f5"+
		"\3\2\2\2\u00f8\u0106\7\b\2\2\u00f9\u00fa\7\7\2\2\u00fa\u00ff\5\"\22\2"+
		"\u00fb\u00fc\7\n\2\2\u00fc\u00fe\5\"\22\2\u00fd\u00fb\3\2\2\2\u00fe\u0101"+
		"\3\2\2\2\u00ff\u00fd\3\2\2\2\u00ff\u0100\3\2\2\2\u0100\u0102\3\2\2\2\u0101"+
		"\u00ff\3\2\2\2\u0102\u0103\7\b\2\2\u0103\u0105\3\2\2\2\u0104\u00f9\3\2"+
		"\2\2\u0105\u0108\3\2\2\2\u0106\u0104\3\2\2\2\u0106\u0107\3\2\2\2\u0107"+
		"/\3\2\2\2\u0108\u0106\3\2\2\2\u0109\u010a\7\36\2\2\u010a\61\3\2\2\2\35"+
		"\67@ENQ\\`gqz\u0083\u0089\u0090\u009a\u00a2\u00a6\u00ac\u00b3\u00b9\u00c2"+
		"\u00d3\u00dc\u00e5\u00ed\u00f5\u00ff\u0106";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}