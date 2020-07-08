// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLangModel.g4 by ANTLR 4.8
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
		T__9=10, T__10=11, T__11=12, T__12=13, TEXT=14, WS=15, STRING=16, NUMBER=17, 
		ID=18;
	public static final int
		RULE_modelBlock = 0, RULE_modelContent = 1, RULE_modelNewEntity = 2, RULE_modelValueType = 3, 
		RULE_modelTbl = 4, RULE_modelEntityAsAttribut = 5, RULE_modelAttribut = 6, 
		RULE_modelSetEntity = 7, RULE_modelSetAttribute = 8, RULE_modelSetValueType = 9, 
		RULE_modelSetType = 10, RULE_modelGeneric = 11, RULE_modelSetFuncDef = 12, 
		RULE_modelSetRef = 13;
	private static String[] makeRuleNames() {
		return new String[] {
			"modelBlock", "modelContent", "modelNewEntity", "modelValueType", "modelTbl", 
			"modelEntityAsAttribut", "modelAttribut", "modelSetEntity", "modelSetAttribute", 
			"modelSetValueType", "modelSetType", "modelGeneric", "modelSetFuncDef", 
			"modelSetRef"
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
			null, null, "TEXT", "WS", "STRING", "NUMBER", "ID"
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
			setState(28);
			match(T__0);
			setState(29);
			match(T__1);
			setState(33);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3 || _la==T__9) {
				{
				{
				setState(30);
				((ModelBlockContext)_localctx).modelContent = modelContent();
				((ModelBlockContext)_localctx).modelContents.add(((ModelBlockContext)_localctx).modelContent);
				}
				}
				setState(35);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(36);
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
			setState(40);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				enterOuterAlt(_localctx, 1);
				{
				setState(38);
				modelNewEntity();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 2);
				{
				setState(39);
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
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
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
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			match(T__3);
			setState(43);
			((ModelNewEntityContext)_localctx).type = match(ID);
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(44);
				match(T__4);
				{
				{
				setState(45);
				((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
				((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(46);
					match(T__5);
					setState(47);
					((ModelNewEntityContext)_localctx).modelAttribut = modelAttribut();
					((ModelNewEntityContext)_localctx).attrs.add(((ModelNewEntityContext)_localctx).modelAttribut);
					}
					}
					setState(52);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(53);
				match(T__6);
				}
			}

			setState(57);
			match(T__1);
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__7) | (1L << STRING) | (1L << ID))) != 0)) {
				{
				{
				setState(58);
				((ModelNewEntityContext)_localctx).modelValueType = modelValueType();
				((ModelNewEntityContext)_localctx).decl.add(((ModelNewEntityContext)_localctx).modelValueType);
				}
				}
				setState(63);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(64);
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
		enterRule(_localctx, 6, RULE_modelValueType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(69);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(66);
				modelAttribut();
				}
				break;
			case 2:
				{
				setState(67);
				modelEntityAsAttribut();
				}
				break;
			case 3:
				{
				setState(68);
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
		enterRule(_localctx, 8, RULE_modelTbl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(71);
				((ModelTblContext)_localctx).attr = match(ID);
				}
			}

			{
			setState(74);
			match(T__7);
			}
			{
			{
			setState(75);
			((ModelTblContext)_localctx).modelValueType = modelValueType();
			((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
			}
			setState(80);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(76);
				match(T__5);
				setState(77);
				((ModelTblContext)_localctx).modelValueType = modelValueType();
				((ModelTblContext)_localctx).elms.add(((ModelTblContext)_localctx).modelValueType);
				}
				}
				setState(82);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
			setState(83);
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

	public static class ModelEntityAsAttributContext extends ParserRuleContext {
		public Token attr;
		public ModelNewEntityContext value;
		public ModelNewEntityContext modelNewEntity() {
			return getRuleContext(ModelNewEntityContext.class,0);
		}
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelEntityAsAttributContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelEntityAsAttribut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelEntityAsAttribut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelEntityAsAttribut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelEntityAsAttribut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelEntityAsAttributContext modelEntityAsAttribut() throws RecognitionException {
		ModelEntityAsAttributContext _localctx = new ModelEntityAsAttributContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_modelEntityAsAttribut);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(85);
				((ModelEntityAsAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(88);
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
		public TerminalNode STRING() { return getToken(TLangModelParser.STRING, 0); }
		public TerminalNode ID() { return getToken(TLangModelParser.ID, 0); }
		public ModelAttributContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelAttribut; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).enterModelAttribut(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangModelListener ) ((TLangModelListener)listener).exitModelAttribut(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangModelVisitor ) return ((TLangModelVisitor<? extends T>)visitor).visitModelAttribut(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelAttributContext modelAttribut() throws RecognitionException {
		ModelAttributContext _localctx = new ModelAttributContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_modelAttribut);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(91);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ID) {
				{
				setState(90);
				((ModelAttributContext)_localctx).attr = match(ID);
				}
			}

			setState(93);
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
		enterRule(_localctx, 14, RULE_modelSetEntity);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(T__9);
			setState(96);
			((ModelSetEntityContext)_localctx).name = match(ID);
			setState(108);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(97);
				match(T__4);
				{
				{
				setState(98);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				setState(103);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(99);
					match(T__5);
					setState(100);
					((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
					((ModelSetEntityContext)_localctx).params.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
					}
					}
					setState(105);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(106);
				match(T__6);
				}
			}

			setState(110);
			match(T__1);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__4) | (1L << T__12) | (1L << ID))) != 0)) {
				{
				{
				setState(111);
				((ModelSetEntityContext)_localctx).modelSetAttribute = modelSetAttribute();
				((ModelSetEntityContext)_localctx).attrs.add(((ModelSetEntityContext)_localctx).modelSetAttribute);
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
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
		enterRule(_localctx, 16, RULE_modelSetAttribute);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(120);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				setState(119);
				((ModelSetAttributeContext)_localctx).attr = match(ID);
				}
				break;
			}
			setState(122);
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
		enterRule(_localctx, 18, RULE_modelSetValueType);
		try {
			setState(127);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(124);
				modelSetType();
				}
				break;
			case T__4:
				enterOuterAlt(_localctx, 2);
				{
				setState(125);
				modelSetFuncDef();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 3);
				{
				setState(126);
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
		enterRule(_localctx, 20, RULE_modelSetType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(129);
			((ModelSetTypeContext)_localctx).type = match(ID);
			setState(134);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__10) {
				{
				setState(130);
				match(T__10);
				{
				setState(131);
				((ModelSetTypeContext)_localctx).generic = modelGeneric();
				}
				setState(132);
				match(T__11);
				}
			}

			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__7) {
				{
				setState(136);
				((ModelSetTypeContext)_localctx).array = match(T__7);
				setState(137);
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
		enterRule(_localctx, 22, RULE_modelGeneric);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(140);
			((ModelGenericContext)_localctx).modelSetType = modelSetType();
			((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
			setState(145);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(141);
				match(T__5);
				setState(142);
				((ModelGenericContext)_localctx).modelSetType = modelSetType();
				((ModelGenericContext)_localctx).types.add(((ModelGenericContext)_localctx).modelSetType);
				}
				}
				setState(147);
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
		enterRule(_localctx, 24, RULE_modelSetFuncDef);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(148);
			match(T__4);
			setState(149);
			match(T__6);
			setState(159);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(150);
				match(T__12);
				setState(151);
				((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
				((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
				setState(156);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(152);
						match(T__5);
						setState(153);
						((ModelSetFuncDefContext)_localctx).modelSetType = modelSetType();
						((ModelSetFuncDefContext)_localctx).retTypes.add(((ModelSetFuncDefContext)_localctx).modelSetType);
						}
						} 
					}
					setState(158);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
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
		enterRule(_localctx, 26, RULE_modelSetRef);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(161);
			match(T__12);
			setState(162);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\24\u00a7\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\7\2\"\n\2\f\2\16"+
		"\2%\13\2\3\2\3\2\3\3\3\3\5\3+\n\3\3\4\3\4\3\4\3\4\3\4\3\4\7\4\63\n\4\f"+
		"\4\16\4\66\13\4\3\4\3\4\5\4:\n\4\3\4\3\4\7\4>\n\4\f\4\16\4A\13\4\3\4\3"+
		"\4\3\5\3\5\3\5\5\5H\n\5\3\6\5\6K\n\6\3\6\3\6\3\6\3\6\7\6Q\n\6\f\6\16\6"+
		"T\13\6\3\6\3\6\3\7\5\7Y\n\7\3\7\3\7\3\b\5\b^\n\b\3\b\3\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\7\th\n\t\f\t\16\tk\13\t\3\t\3\t\5\to\n\t\3\t\3\t\7\ts\n\t\f"+
		"\t\16\tv\13\t\3\t\3\t\3\n\5\n{\n\n\3\n\3\n\3\13\3\13\3\13\5\13\u0082\n"+
		"\13\3\f\3\f\3\f\3\f\3\f\5\f\u0089\n\f\3\f\3\f\5\f\u008d\n\f\3\r\3\r\3"+
		"\r\7\r\u0092\n\r\f\r\16\r\u0095\13\r\3\16\3\16\3\16\3\16\3\16\3\16\7\16"+
		"\u009d\n\16\f\16\16\16\u00a0\13\16\5\16\u00a2\n\16\3\17\3\17\3\17\3\17"+
		"\2\2\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\2\2\u00ae\2\36\3\2\2\2\4"+
		"*\3\2\2\2\6,\3\2\2\2\bG\3\2\2\2\nJ\3\2\2\2\fX\3\2\2\2\16]\3\2\2\2\20a"+
		"\3\2\2\2\22z\3\2\2\2\24\u0081\3\2\2\2\26\u0083\3\2\2\2\30\u008e\3\2\2"+
		"\2\32\u0096\3\2\2\2\34\u00a3\3\2\2\2\36\37\7\3\2\2\37#\7\4\2\2 \"\5\4"+
		"\3\2! \3\2\2\2\"%\3\2\2\2#!\3\2\2\2#$\3\2\2\2$&\3\2\2\2%#\3\2\2\2&\'\7"+
		"\5\2\2\'\3\3\2\2\2(+\5\6\4\2)+\5\20\t\2*(\3\2\2\2*)\3\2\2\2+\5\3\2\2\2"+
		",-\7\6\2\2-9\7\24\2\2./\7\7\2\2/\64\5\16\b\2\60\61\7\b\2\2\61\63\5\16"+
		"\b\2\62\60\3\2\2\2\63\66\3\2\2\2\64\62\3\2\2\2\64\65\3\2\2\2\65\67\3\2"+
		"\2\2\66\64\3\2\2\2\678\7\t\2\28:\3\2\2\29.\3\2\2\29:\3\2\2\2:;\3\2\2\2"+
		";?\7\4\2\2<>\5\b\5\2=<\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@B\3\2\2\2"+
		"A?\3\2\2\2BC\7\5\2\2C\7\3\2\2\2DH\5\16\b\2EH\5\f\7\2FH\5\n\6\2GD\3\2\2"+
		"\2GE\3\2\2\2GF\3\2\2\2H\t\3\2\2\2IK\7\24\2\2JI\3\2\2\2JK\3\2\2\2KL\3\2"+
		"\2\2LM\7\n\2\2MR\5\b\5\2NO\7\b\2\2OQ\5\b\5\2PN\3\2\2\2QT\3\2\2\2RP\3\2"+
		"\2\2RS\3\2\2\2SU\3\2\2\2TR\3\2\2\2UV\7\13\2\2V\13\3\2\2\2WY\7\24\2\2X"+
		"W\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z[\5\6\4\2[\r\3\2\2\2\\^\7\24\2\2]\\\3\2"+
		"\2\2]^\3\2\2\2^_\3\2\2\2_`\7\22\2\2`\17\3\2\2\2ab\7\f\2\2bn\7\24\2\2c"+
		"d\7\7\2\2di\5\22\n\2ef\7\b\2\2fh\5\22\n\2ge\3\2\2\2hk\3\2\2\2ig\3\2\2"+
		"\2ij\3\2\2\2jl\3\2\2\2ki\3\2\2\2lm\7\t\2\2mo\3\2\2\2nc\3\2\2\2no\3\2\2"+
		"\2op\3\2\2\2pt\7\4\2\2qs\5\22\n\2rq\3\2\2\2sv\3\2\2\2tr\3\2\2\2tu\3\2"+
		"\2\2uw\3\2\2\2vt\3\2\2\2wx\7\5\2\2x\21\3\2\2\2y{\7\24\2\2zy\3\2\2\2z{"+
		"\3\2\2\2{|\3\2\2\2|}\5\24\13\2}\23\3\2\2\2~\u0082\5\26\f\2\177\u0082\5"+
		"\32\16\2\u0080\u0082\5\34\17\2\u0081~\3\2\2\2\u0081\177\3\2\2\2\u0081"+
		"\u0080\3\2\2\2\u0082\25\3\2\2\2\u0083\u0088\7\24\2\2\u0084\u0085\7\r\2"+
		"\2\u0085\u0086\5\30\r\2\u0086\u0087\7\16\2\2\u0087\u0089\3\2\2\2\u0088"+
		"\u0084\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008c\3\2\2\2\u008a\u008b\7\n"+
		"\2\2\u008b\u008d\7\13\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d"+
		"\27\3\2\2\2\u008e\u0093\5\26\f\2\u008f\u0090\7\b\2\2\u0090\u0092\5\26"+
		"\f\2\u0091\u008f\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093"+
		"\u0094\3\2\2\2\u0094\31\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0097\7\7\2"+
		"\2\u0097\u00a1\7\t\2\2\u0098\u0099\7\17\2\2\u0099\u009e\5\26\f\2\u009a"+
		"\u009b\7\b\2\2\u009b\u009d\5\26\f\2\u009c\u009a\3\2\2\2\u009d\u00a0\3"+
		"\2\2\2\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a2\3\2\2\2\u00a0"+
		"\u009e\3\2\2\2\u00a1\u0098\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\33\3\2\2"+
		"\2\u00a3\u00a4\7\17\2\2\u00a4\u00a5\7\24\2\2\u00a5\35\3\2\2\2\26#*\64"+
		"9?GJRX]intz\u0081\u0088\u008c\u0093\u009e\u00a1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}