// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLangHelper.g4 by ANTLR 4.8
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
		T__17=18, TEXT=19, ID=20, WS=21, STRING=22, NUMBER=23;
	public static final int
		RULE_helperBlock = 0, RULE_helperFunc = 1, RULE_helperContent = 2, RULE_helperStatement = 3, 
		RULE_helperIf = 4, RULE_helperElse = 5, RULE_helperCondition = 6, RULE_conditionMark = 7, 
		RULE_helperFor = 8, RULE_helperCallObj = 9, RULE_helperCallArray = 10, 
		RULE_helperCallFunc = 11, RULE_helperCallVariable = 12;
	private static String[] makeRuleNames() {
		return new String[] {
			"helperBlock", "helperFunc", "helperContent", "helperStatement", "helperIf", 
			"helperElse", "helperCondition", "conditionMark", "helperFor", "helperCallObj", 
			"helperCallArray", "helperCallFunc", "helperCallVariable"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'helper'", "'{'", "'}'", "'func'", "'if'", "'('", "')'", "'else'", 
			"'=='", "'!='", "'<'", "'>'", "'<='", "'>='", "'for'", "'in'", "'['", 
			"']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, "TEXT", "ID", "WS", "STRING", 
			"NUMBER"
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
			setState(26);
			match(T__0);
			setState(27);
			match(T__1);
			setState(31);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(28);
				((HelperBlockContext)_localctx).helperFunc = helperFunc();
				((HelperBlockContext)_localctx).helperFuncs.add(((HelperBlockContext)_localctx).helperFunc);
				}
				}
				setState(33);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(34);
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
		public HelperContentContext body;
		public TerminalNode ID() { return getToken(TLangHelperParser.ID, 0); }
		public HelperContentContext helperContent() {
			return getRuleContext(HelperContentContext.class,0);
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
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			match(T__3);
			setState(37);
			((HelperFuncContext)_localctx).name = match(ID);
			setState(38);
			match(T__1);
			setState(39);
			((HelperFuncContext)_localctx).body = helperContent();
			setState(40);
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
		enterRule(_localctx, 4, RULE_helperContent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__14) | (1L << ID))) != 0)) {
				{
				{
				setState(42);
				((HelperContentContext)_localctx).helperStatement = helperStatement();
				((HelperContentContext)_localctx).content.add(((HelperContentContext)_localctx).helperStatement);
				}
				}
				setState(47);
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
		enterRule(_localctx, 6, RULE_helperStatement);
		try {
			setState(51);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__4:
				enterOuterAlt(_localctx, 1);
				{
				setState(48);
				helperIf();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 2);
				{
				setState(49);
				helperFor();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(50);
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
		enterRule(_localctx, 8, RULE_helperIf);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53);
			match(T__4);
			setState(54);
			match(T__5);
			setState(55);
			((HelperIfContext)_localctx).condition = helperCondition();
			setState(56);
			match(T__6);
			setState(57);
			match(T__1);
			setState(58);
			((HelperIfContext)_localctx).body = helperContent();
			setState(59);
			match(T__2);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(60);
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
		enterRule(_localctx, 10, RULE_helperElse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			match(T__7);
			setState(64);
			match(T__1);
			setState(65);
			((HelperElseContext)_localctx).body = helperContent();
			setState(66);
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
		enterRule(_localctx, 12, RULE_helperCondition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68);
			((HelperConditionContext)_localctx).arg1 = helperCallObj();
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__12))) != 0)) {
				{
				setState(69);
				((HelperConditionContext)_localctx).mark = conditionMark();
				setState(70);
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
		enterRule(_localctx, 14, RULE_conditionMark);
		try {
			setState(80);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				enterOuterAlt(_localctx, 1);
				{
				setState(74);
				match(T__8);
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(75);
				match(T__9);
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 3);
				{
				setState(76);
				match(T__10);
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 4);
				{
				setState(77);
				match(T__11);
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 5);
				{
				setState(78);
				match(T__12);
				setState(79);
				match(T__13);
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
		public List<TerminalNode> ID() { return getTokens(TLangHelperParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(TLangHelperParser.ID, i);
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
		enterRule(_localctx, 16, RULE_helperFor);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(T__14);
			setState(83);
			match(T__5);
			setState(84);
			((HelperForContext)_localctx).var = match(ID);
			setState(85);
			match(T__15);
			setState(86);
			((HelperForContext)_localctx).array = match(ID);
			setState(87);
			match(T__6);
			setState(88);
			match(T__1);
			setState(89);
			((HelperForContext)_localctx).body = helperContent();
			setState(90);
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
		enterRule(_localctx, 18, RULE_helperCallObj);
		try {
			setState(95);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(92);
				helperCallArray();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(93);
				helperCallFunc();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(94);
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
		enterRule(_localctx, 20, RULE_helperCallArray);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			((HelperCallArrayContext)_localctx).name = match(ID);
			setState(98);
			match(T__16);
			setState(99);
			((HelperCallArrayContext)_localctx).elem = match(ID);
			setState(100);
			match(T__17);
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
		enterRule(_localctx, 22, RULE_helperCallFunc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			((HelperCallFuncContext)_localctx).name = match(ID);
			setState(103);
			match(T__5);
			setState(104);
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
		enterRule(_localctx, 24, RULE_helperCallVariable);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(106);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\31o\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4"+
		"\f\t\f\4\r\t\r\4\16\t\16\3\2\3\2\3\2\7\2 \n\2\f\2\16\2#\13\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\4\7\4.\n\4\f\4\16\4\61\13\4\3\5\3\5\3\5\5\5\66"+
		"\n\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\5\6@\n\6\3\7\3\7\3\7\3\7\3\7\3\b"+
		"\3\b\3\b\3\b\5\bK\n\b\3\t\3\t\3\t\3\t\3\t\3\t\5\tS\n\t\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\5\13b\n\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\r\3\r\3\r\3\r\3\16\3\16\3\16\2\2\17\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\2\2\2m\2\34\3\2\2\2\4&\3\2\2\2\6/\3\2\2\2\b\65\3\2\2\2\n\67\3\2\2\2\f"+
		"A\3\2\2\2\16F\3\2\2\2\20R\3\2\2\2\22T\3\2\2\2\24a\3\2\2\2\26c\3\2\2\2"+
		"\30h\3\2\2\2\32l\3\2\2\2\34\35\7\3\2\2\35!\7\4\2\2\36 \5\4\3\2\37\36\3"+
		"\2\2\2 #\3\2\2\2!\37\3\2\2\2!\"\3\2\2\2\"$\3\2\2\2#!\3\2\2\2$%\7\5\2\2"+
		"%\3\3\2\2\2&\'\7\6\2\2\'(\7\26\2\2()\7\4\2\2)*\5\6\4\2*+\7\5\2\2+\5\3"+
		"\2\2\2,.\5\b\5\2-,\3\2\2\2.\61\3\2\2\2/-\3\2\2\2/\60\3\2\2\2\60\7\3\2"+
		"\2\2\61/\3\2\2\2\62\66\5\n\6\2\63\66\5\22\n\2\64\66\5\24\13\2\65\62\3"+
		"\2\2\2\65\63\3\2\2\2\65\64\3\2\2\2\66\t\3\2\2\2\678\7\7\2\289\7\b\2\2"+
		"9:\5\16\b\2:;\7\t\2\2;<\7\4\2\2<=\5\6\4\2=?\7\5\2\2>@\5\f\7\2?>\3\2\2"+
		"\2?@\3\2\2\2@\13\3\2\2\2AB\7\n\2\2BC\7\4\2\2CD\5\6\4\2DE\7\5\2\2E\r\3"+
		"\2\2\2FJ\5\24\13\2GH\5\20\t\2HI\5\24\13\2IK\3\2\2\2JG\3\2\2\2JK\3\2\2"+
		"\2K\17\3\2\2\2LS\7\13\2\2MS\7\f\2\2NS\7\r\2\2OS\7\16\2\2PQ\7\17\2\2QS"+
		"\7\20\2\2RL\3\2\2\2RM\3\2\2\2RN\3\2\2\2RO\3\2\2\2RP\3\2\2\2S\21\3\2\2"+
		"\2TU\7\21\2\2UV\7\b\2\2VW\7\26\2\2WX\7\22\2\2XY\7\26\2\2YZ\7\t\2\2Z[\7"+
		"\4\2\2[\\\5\6\4\2\\]\7\5\2\2]\23\3\2\2\2^b\5\26\f\2_b\5\30\r\2`b\5\32"+
		"\16\2a^\3\2\2\2a_\3\2\2\2a`\3\2\2\2b\25\3\2\2\2cd\7\26\2\2de\7\23\2\2"+
		"ef\7\26\2\2fg\7\24\2\2g\27\3\2\2\2hi\7\26\2\2ij\7\b\2\2jk\7\t\2\2k\31"+
		"\3\2\2\2lm\7\26\2\2m\33\3\2\2\2\t!/\65?JRa";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}