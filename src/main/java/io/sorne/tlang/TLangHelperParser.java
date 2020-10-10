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
		TEXT=18, ID=19, WS=20, STRING=21, NUMBER=22;
	public static final int
		RULE_helperBlock = 0, RULE_helperFunc = 1, RULE_helperStatement = 2, RULE_helperIf = 3, 
		RULE_helperCondition = 4, RULE_conditionMark = 5, RULE_helperFor = 6, 
		RULE_helperCallObj = 7, RULE_helperCallArray = 8, RULE_helperCallFunc = 9, 
		RULE_helperCallVariable = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"helperBlock", "helperFunc", "helperStatement", "helperIf", "helperCondition", 
			"conditionMark", "helperFor", "helperCallObj", "helperCallArray", "helperCallFunc", 
			"helperCallVariable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'helper'", "'{'", "'}'", "'func'", "'if'", "'('", "')'", "'=='", 
			"'!='", "'<'", "'>'", "'<='", "'>='", "'for'", "'in'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "TEXT", "ID", "WS", "STRING", "NUMBER"
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
			setState(22);
			match(T__0);
			setState(23);
			match(T__1);
			setState(27);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(24);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(29);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(30);
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
		public HelperStatementContext helperStatement;
		public List<HelperStatementContext> content = new ArrayList<HelperStatementContext>();
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public List<HelperStatementContext> helperStatement() {
			return getRuleContexts(HelperStatementContext.class);
		}
		public HelperStatementContext helperStatement(int i) {
			return getRuleContext(HelperStatementContext.class,i);
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
			setState(32);
			match(T__3);
			setState(33);
			((HelperFuncContext)_localctx).name = match(ID);
			setState(34);
			match(T__1);
			setState(38);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__13) | (1L << ID))) != 0)) {
				{
				{
				setState(35);
				((HelperFuncContext)_localctx).helperStatement = helperStatement();
				((HelperFuncContext)_localctx).content.add(((HelperFuncContext)_localctx).helperStatement);
				}
				}
				setState(40);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(41);
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
		enterRule(_localctx, 4, RULE_helperStatement);
		try {
			setState(46);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__4:
				enterOuterAlt(_localctx, 1);
				{
				setState(43);
				helperIf();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 2);
				{
				setState(44);
				helperFor();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(45);
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
		public HelperStatementContext helperStatement;
		public List<HelperStatementContext> content = new ArrayList<HelperStatementContext>();
		public HelperConditionContext helperCondition() {
			return getRuleContext(HelperConditionContext.class,0);
		}
		public List<HelperStatementContext> helperStatement() {
			return getRuleContexts(HelperStatementContext.class);
		}
		public HelperStatementContext helperStatement(int i) {
			return getRuleContext(HelperStatementContext.class,i);
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
		enterRule(_localctx, 6, RULE_helperIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(48);
			match(T__4);
			setState(49);
			match(T__5);
			setState(50);
			((HelperIfContext)_localctx).condition = helperCondition();
			setState(51);
			match(T__6);
			setState(52);
			match(T__1);
			setState(56);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__13) | (1L << ID))) != 0)) {
				{
				{
				setState(53);
				((HelperIfContext)_localctx).helperStatement = helperStatement();
				((HelperIfContext)_localctx).content.add(((HelperIfContext)_localctx).helperStatement);
				}
				}
				setState(58);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(59);
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
		enterRule(_localctx, 8, RULE_helperCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			((HelperConditionContext)_localctx).arg1 = helperCallObj();
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11))) != 0)) {
				{
				setState(62);
				((HelperConditionContext)_localctx).mark = conditionMark();
				setState(63);
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
		enterRule(_localctx, 10, RULE_conditionMark);
		try {
			setState(73);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(67);
				match(T__7);
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 2);
				{
				setState(68);
				match(T__8);
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(69);
				match(T__9);
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(70);
				match(T__10);
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(71);
				match(T__11);
				setState(72);
				match(T__12);
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
		public HelperStatementContext helperStatement;
		public List<HelperStatementContext> content = new ArrayList<HelperStatementContext>();
		public List<TerminalNode> ID() { return getTokens(TLangHelperParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangHelperParser.ID, i);
		}
		public List<HelperStatementContext> helperStatement() {
			return getRuleContexts(HelperStatementContext.class);
		}
		public HelperStatementContext helperStatement(int i) {
			return getRuleContext(HelperStatementContext.class,i);
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
		enterRule(_localctx, 12, RULE_helperFor);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(T__13);
			setState(76);
			match(T__5);
			setState(77);
			((HelperForContext)_localctx).var = match(ID);
			setState(78);
			match(T__14);
			setState(79);
			((HelperForContext)_localctx).array = match(ID);
			setState(80);
			match(T__6);
			setState(81);
			match(T__1);
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__13) | (1L << ID))) != 0)) {
				{
				{
				setState(82);
				((HelperForContext)_localctx).helperStatement = helperStatement();
				((HelperForContext)_localctx).content.add(((HelperForContext)_localctx).helperStatement);
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(88);
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
		enterRule(_localctx, 14, RULE_helperCallObj);
		try {
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(90);
				helperCallArray();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(91);
				helperCallFunc();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(92);
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
		public List<TerminalNode> ID() { return getTokens(TLangHelperParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangHelperParser.ID, i);
		}
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
		enterRule(_localctx, 16, RULE_helperCallArray);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			((HelperCallArrayContext)_localctx).name = match(ID);
			setState(96);
			match(T__15);
			setState(97);
			((HelperCallArrayContext)_localctx).elem = match(ID);
			setState(98);
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
		enterRule(_localctx, 18, RULE_helperCallFunc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(100);
			((HelperCallFuncContext)_localctx).name = match(ID);
			setState(101);
			match(T__5);
			setState(102);
			match(T__6);
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
		enterRule(_localctx, 20, RULE_helperCallVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\30m\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\3\2\3\2\3\2\7\2\34\n\2\f\2\16\2\37\13\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\7\3\'\n\3\f\3\16\3*\13\3\3\3\3\3\3\4\3\4\3\4\5\4\61\n\4\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\7\59\n\5\f\5\16\5<\13\5\3\5\3\5\3\6\3\6\3\6\3\6\5\6D\n\6\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\5\7L\n\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\bV\n"+
		"\b\f\b\16\bY\13\b\3\b\3\b\3\t\3\t\3\t\5\t`\n\t\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\2\2\r\2\4\6\b\n\f\16\20\22\24\26\2\2\2n\2"+
		"\30\3\2\2\2\4\"\3\2\2\2\6\60\3\2\2\2\b\62\3\2\2\2\n?\3\2\2\2\fK\3\2\2"+
		"\2\16M\3\2\2\2\20_\3\2\2\2\22a\3\2\2\2\24f\3\2\2\2\26j\3\2\2\2\30\31\7"+
		"\3\2\2\31\35\7\4\2\2\32\34\5\4\3\2\33\32\3\2\2\2\34\37\3\2\2\2\35\33\3"+
		"\2\2\2\35\36\3\2\2\2\36 \3\2\2\2\37\35\3\2\2\2 !\7\5\2\2!\3\3\2\2\2\""+
		"#\7\6\2\2#$\7\25\2\2$(\7\4\2\2%\'\5\6\4\2&%\3\2\2\2\'*\3\2\2\2(&\3\2\2"+
		"\2()\3\2\2\2)+\3\2\2\2*(\3\2\2\2+,\7\5\2\2,\5\3\2\2\2-\61\5\b\5\2.\61"+
		"\5\16\b\2/\61\5\20\t\2\60-\3\2\2\2\60.\3\2\2\2\60/\3\2\2\2\61\7\3\2\2"+
		"\2\62\63\7\7\2\2\63\64\7\b\2\2\64\65\5\n\6\2\65\66\7\t\2\2\66:\7\4\2\2"+
		"\679\5\6\4\28\67\3\2\2\29<\3\2\2\2:8\3\2\2\2:;\3\2\2\2;=\3\2\2\2<:\3\2"+
		"\2\2=>\7\5\2\2>\t\3\2\2\2?C\5\20\t\2@A\5\f\7\2AB\5\20\t\2BD\3\2\2\2C@"+
		"\3\2\2\2CD\3\2\2\2D\13\3\2\2\2EL\7\n\2\2FL\7\13\2\2GL\7\f\2\2HL\7\r\2"+
		"\2IJ\7\16\2\2JL\7\17\2\2KE\3\2\2\2KF\3\2\2\2KG\3\2\2\2KH\3\2\2\2KI\3\2"+
		"\2\2L\r\3\2\2\2MN\7\20\2\2NO\7\b\2\2OP\7\25\2\2PQ\7\21\2\2QR\7\25\2\2"+
		"RS\7\t\2\2SW\7\4\2\2TV\5\6\4\2UT\3\2\2\2VY\3\2\2\2WU\3\2\2\2WX\3\2\2\2"+
		"XZ\3\2\2\2YW\3\2\2\2Z[\7\5\2\2[\17\3\2\2\2\\`\5\22\n\2]`\5\24\13\2^`\5"+
		"\26\f\2_\\\3\2\2\2_]\3\2\2\2_^\3\2\2\2`\21\3\2\2\2ab\7\25\2\2bc\7\22\2"+
		"\2cd\7\25\2\2de\7\23\2\2e\23\3\2\2\2fg\7\25\2\2gh\7\b\2\2hi\7\t\2\2i\25"+
		"\3\2\2\2jk\7\25\2\2k\27\3\2\2\2\n\35(\60:CKW_";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}