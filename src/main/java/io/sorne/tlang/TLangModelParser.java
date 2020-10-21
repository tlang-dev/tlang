// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLangModel.g4 by ANTLR 4.8
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
public class TLangModelParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, TEXT=14, ID=15, WS=16, STRING=17, 
		NUMBER=18;
	public static final int
		RULE_modelBlock = 0, RULE_modelContent = 1, RULE_modelNewEntity = 2, RULE_modelNewEntityValue = 3, 
		RULE_modelValueType = 4, RULE_modelTbl = 5, RULE_modelEntityAsAttribute = 6, 
		RULE_modelAttribute = 7, RULE_modelSetEntity = 8, RULE_modelSetAttribute = 9, 
		RULE_modelSetValueType = 10, RULE_modelSetType = 11, RULE_modelGeneric = 12, 
		RULE_modelSetFuncDef = 13, RULE_modelSetRef = 14;
	private static String[] makeRuleNames() {
		return new String[] {
			"modelBlock", "modelContent", "modelNewEntity", "modelNewEntityValue", 
			"modelValueType", "modelTbl", "modelEntityAsAttribute", "modelAttribute", 
			"modelSetEntity", "modelSetAttribute", "modelSetValueType", "modelSetType", 
			"modelGeneric", "modelSetFuncDef", "modelSetRef"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'model'", "'{'", "'}'", "'let'", "'('", "','", "')'", "'['", "']'", 
			"'set'", "'<'", "'>'", "'->'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, "TEXT", "ID", "WS", "STRING", "NUMBER"
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
	public String getGrammarFileName() { return "TLangModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public TLangModelParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelBlockContext modelBlock() throws RecognitionException {
		ModelBlockContext _localctx = new ModelBlockContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_modelBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			match(T__0);
			setState(31);
			match(T__1);
			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__9) {
				{
				{
				setState(32);
				((ModelBlockContext)_localctx).modelContent = modelContent();
				((ModelBlockContext)_localctx).modelContents.add(((ModelBlockContext)_localctx).modelContent);
				}
				}
				setState(37);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(38);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelContent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelContent(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelContent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelContentContext modelContent() throws RecognitionException {
		ModelContentContext _localctx = new ModelContentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_modelContent);
		try {
			setState(42);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(40);
				modelNewEntity();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(41);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelNewEntityValueContext modelNewEntityValue() {
			return getRuleContext(ModelNewEntityValueContext.class,0);
		}
		public ModelNewEntityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelNewEntity; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelNewEntity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelNewEntity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelNewEntity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelNewEntityContext modelNewEntity() throws RecognitionException {
		ModelNewEntityContext _localctx = new ModelNewEntityContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_modelNewEntity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(44);
			match(T__3);
			setState(45);
			((ModelNewEntityContext)_localctx).name = match(ID);
			setState(46);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelNewEntityValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelNewEntityValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelNewEntityValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelNewEntityValueContext modelNewEntityValue() throws RecognitionException {
		ModelNewEntityValueContext _localctx = new ModelNewEntityValueContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_modelNewEntityValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(48);
				((ModelNewEntityValueContext)_localctx).type = match(ID);
				}
			}

			setState(62);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(51);
				match(T__4);
				{
				{
				setState(52);
				((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityValueContext)_localctx).attrs.add(((ModelNewEntityValueContext)_localctx).modelValueType);
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(53);
					match(T__5);
					setState(54);
					((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
					((ModelNewEntityValueContext)_localctx).attrs.add(((ModelNewEntityValueContext)_localctx).modelValueType);
					}
					}
					setState(59);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(60);
				match(T__6);
				}
			}

			setState(64);
			match(T__1);
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__4) | (1L << T__7) | (1L << ID) | (1L << STRING))) != 0)) {
				{
				{
				setState(65);
				((ModelNewEntityValueContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityValueContext)_localctx).decl.add(((ModelNewEntityValueContext)_localctx).modelValueType);
				}
				}
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(71);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelValueType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelValueType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelValueType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelValueTypeContext modelValueType() throws RecognitionException {
		ModelValueTypeContext _localctx = new ModelValueTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_modelValueType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(73);
				modelAttribute();
				}
				break;
			case 2:
				{
				setState(74);
				modelEntityAsAttribute();
				}
				break;
			case 3:
				{
				setState(75);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelTbl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelTbl(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelTbl(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelTblContext modelTbl() throws RecognitionException {
		ModelTblContext _localctx = new ModelTblContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_modelTbl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(78);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(81);
			match(T__7);
			}
			{
			{
			setState(82);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(83);
				match(T__5);
				setState(84);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(89);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(90);
			match(T__8);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelEntityAsAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelEntityAsAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelEntityAsAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelEntityAsAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelEntityAsAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelEntityAsAttributeContext modelEntityAsAttribute() throws RecognitionException {
		ModelEntityAsAttributeContext _localctx = new ModelEntityAsAttributeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_modelEntityAsAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(93);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(92);
				((ModelEntityAsAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(95);
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
		public TerminalNode STRING() { return getToken(TLangModelParser.STRING, 0); }
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelAttributeContext modelAttribute() throws RecognitionException {
		ModelAttributeContext _localctx = new ModelAttributeContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_modelAttribute);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(97);
				((ModelAttributeContext)_localctx).attr = match(ID);
				}
			}

			setState(100);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetEntity(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetEntity(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetEntity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetEntityContext modelSetEntity() throws RecognitionException {
		ModelSetEntityContext _localctx = new ModelSetEntityContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_modelSetEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102);
			match(T__9);
			setState(103);
			((ModelSetEntityContext)_localctx).name = match(ID);
			setState(115);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(104);
				match(T__4);
				{
				{
				setState(105);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				setState(110);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(106);
					match(T__5);
					setState(107);
					((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
					((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
					}
					}
					setState(112);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(113);
				match(T__6);
				}
			}

			setState(117);
			match(T__1);
			setState(121);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__12) | (1L << ID))) != 0)) {
				{
				{
				setState(118);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).attrs.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				}
				setState(123);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(124);
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

	public static class ModelSetAttributeContext extends ParserRuleContext {
		public Token attr;
		public ModelSetValueTypeContext value;
		public ModelSetValueTypeContext modelSetValueType() {
			return getRuleContext(ModelSetValueTypeContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelSetAttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetAttribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetAttribute(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetAttribute(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetAttributeContext modelSetAttribute() throws RecognitionException {
		ModelSetAttributeContext _localctx = new ModelSetAttributeContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_modelSetAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				setState(126);
				((ModelSetAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(129);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetValueType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetValueType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetValueType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetValueTypeContext modelSetValueType() throws RecognitionException {
		ModelSetValueTypeContext _localctx = new ModelSetValueTypeContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_modelSetValueType);
		try {
			setState(134);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(131);
				modelSetType();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				setState(132);
				modelSetFuncDef();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(133);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelGenericContext modelGeneric() {
			return getRuleContext(ModelGenericContext.class,0);
		}
		public ModelSetTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetTypeContext modelSetType() throws RecognitionException {
		ModelSetTypeContext _localctx = new ModelSetTypeContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_modelSetType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			((ModelSetTypeContext)_localctx).type = match(ID);
			setState(141);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(137);
				match(T__10);
				{
				setState(138);
				((ModelSetTypeContext)_localctx).generic = modelGeneric();
				}
				setState(139);
				match(T__11);
				}
			}

			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(143);
				((ModelSetTypeContext)_localctx).array = match(T__7);
				setState(144);
				match(T__8);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelGeneric(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelGeneric(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelGeneric(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelGenericContext modelGeneric() throws RecognitionException {
		ModelGenericContext _localctx = new ModelGenericContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_modelGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(147);
			((ModelGenericContext)_localctx).modelSetType = modelSetType();
			((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
			setState(152);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(148);
				match(T__5);
				setState(149);
				((ModelGenericContext)_localctx).modelSetType = modelSetType();
				((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
				}
				}
				setState(154);
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
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetFuncDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetFuncDef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetFuncDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetFuncDefContext modelSetFuncDef() throws RecognitionException {
		ModelSetFuncDefContext _localctx = new ModelSetFuncDefContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_modelSetFuncDef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(155);
			match(T__4);
			setState(156);
			match(T__6);
			setState(166);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				{
				setState(157);
				match(T__12);
				setState(158);
				((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
				((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
				setState(163);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(159);
						match(T__5);
						setState(160);
						((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
						((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
						}
						} 
					}
					setState(165);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelSetRefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelSetRef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelSetRef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelSetRef(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelSetRef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelSetRefContext modelSetRef() throws RecognitionException {
		ModelSetRefContext _localctx = new ModelSetRefContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_modelSetRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			match(T__12);
			setState(169);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24\u00ae\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\7\2$\n"+
		"\2\f\2\16\2\'\13\2\3\2\3\2\3\3\3\3\5\3-\n\3\3\4\3\4\3\4\3\4\3\5\5\5\64"+
		"\n\5\3\5\3\5\3\5\3\5\7\5:\n\5\f\5\16\5=\13\5\3\5\3\5\5\5A\n\5\3\5\3\5"+
		"\7\5E\n\5\f\5\16\5H\13\5\3\5\3\5\3\6\3\6\3\6\5\6O\n\6\3\7\5\7R\n\7\3\7"+
		"\3\7\3\7\3\7\7\7X\n\7\f\7\16\7[\13\7\3\7\3\7\3\b\5\b`\n\b\3\b\3\b\3\t"+
		"\5\te\n\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\7\no\n\n\f\n\16\nr\13\n\3\n"+
		"\3\n\5\nv\n\n\3\n\3\n\7\nz\n\n\f\n\16\n}\13\n\3\n\3\n\3\13\5\13\u0082"+
		"\n\13\3\13\3\13\3\f\3\f\3\f\5\f\u0089\n\f\3\r\3\r\3\r\3\r\3\r\5\r\u0090"+
		"\n\r\3\r\3\r\5\r\u0094\n\r\3\16\3\16\3\16\7\16\u0099\n\16\f\16\16\16\u009c"+
		"\13\16\3\17\3\17\3\17\3\17\3\17\3\17\7\17\u00a4\n\17\f\17\16\17\u00a7"+
		"\13\17\5\17\u00a9\n\17\3\20\3\20\3\20\3\20\2\2\21\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36\2\2\2\u00b5\2 \3\2\2\2\4,\3\2\2\2\6.\3\2\2\2\b\63\3"+
		"\2\2\2\nN\3\2\2\2\fQ\3\2\2\2\16_\3\2\2\2\20d\3\2\2\2\22h\3\2\2\2\24\u0081"+
		"\3\2\2\2\26\u0088\3\2\2\2\30\u008a\3\2\2\2\32\u0095\3\2\2\2\34\u009d\3"+
		"\2\2\2\36\u00aa\3\2\2\2 !\7\3\2\2!%\7\4\2\2\"$\5\4\3\2#\"\3\2\2\2$\'\3"+
		"\2\2\2%#\3\2\2\2%&\3\2\2\2&(\3\2\2\2\'%\3\2\2\2()\7\5\2\2)\3\3\2\2\2*"+
		"-\5\6\4\2+-\5\22\n\2,*\3\2\2\2,+\3\2\2\2-\5\3\2\2\2./\7\6\2\2/\60\7\21"+
		"\2\2\60\61\5\b\5\2\61\7\3\2\2\2\62\64\7\21\2\2\63\62\3\2\2\2\63\64\3\2"+
		"\2\2\64@\3\2\2\2\65\66\7\7\2\2\66;\5\n\6\2\678\7\b\2\28:\5\n\6\29\67\3"+
		"\2\2\2:=\3\2\2\2;9\3\2\2\2;<\3\2\2\2<>\3\2\2\2=;\3\2\2\2>?\7\t\2\2?A\3"+
		"\2\2\2@\65\3\2\2\2@A\3\2\2\2AB\3\2\2\2BF\7\4\2\2CE\5\n\6\2DC\3\2\2\2E"+
		"H\3\2\2\2FD\3\2\2\2FG\3\2\2\2GI\3\2\2\2HF\3\2\2\2IJ\7\5\2\2J\t\3\2\2\2"+
		"KO\5\20\t\2LO\5\16\b\2MO\5\f\7\2NK\3\2\2\2NL\3\2\2\2NM\3\2\2\2O\13\3\2"+
		"\2\2PR\7\21\2\2QP\3\2\2\2QR\3\2\2\2RS\3\2\2\2ST\7\n\2\2TY\5\n\6\2UV\7"+
		"\b\2\2VX\5\n\6\2WU\3\2\2\2X[\3\2\2\2YW\3\2\2\2YZ\3\2\2\2Z\\\3\2\2\2[Y"+
		"\3\2\2\2\\]\7\13\2\2]\r\3\2\2\2^`\7\21\2\2_^\3\2\2\2_`\3\2\2\2`a\3\2\2"+
		"\2ab\5\b\5\2b\17\3\2\2\2ce\7\21\2\2dc\3\2\2\2de\3\2\2\2ef\3\2\2\2fg\7"+
		"\23\2\2g\21\3\2\2\2hi\7\f\2\2iu\7\21\2\2jk\7\7\2\2kp\5\24\13\2lm\7\b\2"+
		"\2mo\5\24\13\2nl\3\2\2\2or\3\2\2\2pn\3\2\2\2pq\3\2\2\2qs\3\2\2\2rp\3\2"+
		"\2\2st\7\t\2\2tv\3\2\2\2uj\3\2\2\2uv\3\2\2\2vw\3\2\2\2w{\7\4\2\2xz\5\24"+
		"\13\2yx\3\2\2\2z}\3\2\2\2{y\3\2\2\2{|\3\2\2\2|~\3\2\2\2}{\3\2\2\2~\177"+
		"\7\5\2\2\177\23\3\2\2\2\u0080\u0082\7\21\2\2\u0081\u0080\3\2\2\2\u0081"+
		"\u0082\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\5\26\f\2\u0084\25\3\2\2"+
		"\2\u0085\u0089\5\30\r\2\u0086\u0089\5\34\17\2\u0087\u0089\5\36\20\2\u0088"+
		"\u0085\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0087\3\2\2\2\u0089\27\3\2\2"+
		"\2\u008a\u008f\7\21\2\2\u008b\u008c\7\r\2\2\u008c\u008d\5\32\16\2\u008d"+
		"\u008e\7\16\2\2\u008e\u0090\3\2\2\2\u008f\u008b\3\2\2\2\u008f\u0090\3"+
		"\2\2\2\u0090\u0093\3\2\2\2\u0091\u0092\7\n\2\2\u0092\u0094\7\13\2\2\u0093"+
		"\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\31\3\2\2\2\u0095\u009a\5\30\r"+
		"\2\u0096\u0097\7\b\2\2\u0097\u0099\5\30\r\2\u0098\u0096\3\2\2\2\u0099"+
		"\u009c\3\2\2\2\u009a\u0098\3\2\2\2\u009a\u009b\3\2\2\2\u009b\33\3\2\2"+
		"\2\u009c\u009a\3\2\2\2\u009d\u009e\7\7\2\2\u009e\u00a8\7\t\2\2\u009f\u00a0"+
		"\7\17\2\2\u00a0\u00a5\5\30\r\2\u00a1\u00a2\7\b\2\2\u00a2\u00a4\5\30\r"+
		"\2\u00a3\u00a1\3\2\2\2\u00a4\u00a7\3\2\2\2\u00a5\u00a3\3\2\2\2\u00a5\u00a6"+
		"\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3\2\2\2\u00a8\u009f\3\2\2\2\u00a8"+
		"\u00a9\3\2\2\2\u00a9\35\3\2\2\2\u00aa\u00ab\7\17\2\2\u00ab\u00ac\7\21"+
		"\2\2\u00ac\37\3\2\2\2\27%,\63;@FNQY_dpu{\u0081\u0088\u008f\u0093\u009a"+
		"\u00a5\u00a8";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}