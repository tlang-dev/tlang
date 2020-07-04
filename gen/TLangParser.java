// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
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
		STRING=1, Lang=2, File=3, HelperBlock=4, HelperFunc=5, TmplBlock=6, TmplPkg=7, 
		TmplUse=8, TmplImpl=9, TmplFunc=10, TmplCurrying=11, TmplCurryingParam=12, 
		TmplParam=13, TmplType=14, TmplGeneric=15, TmplExpression=16, TmplVal=17, 
		TmplVar=18, ModelBlock=19, ModelNewEntity=20, ModelValueType=21, ModelTbl=22, 
		ModelEntityAsAttribut=23, ModelAttribut=24, Text=25, AnyID=26, ID=27, 
		ID_RPL=28;
	public static final int
		RULE_domainmodel = 0;
	private static String[] makeRuleNames() {
		return new String[] {
			"domainmodel"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "STRING", "Lang", "File", "HelperBlock", "HelperFunc", "TmplBlock", 
			"TmplPkg", "TmplUse", "TmplImpl", "TmplFunc", "TmplCurrying", "TmplCurryingParam", 
			"TmplParam", "TmplType", "TmplGeneric", "TmplExpression", "TmplVal", 
			"TmplVar", "ModelBlock", "ModelNewEntity", "ModelValueType", "ModelTbl", 
			"ModelEntityAsAttribut", "ModelAttribut", "Text", "AnyID", "ID", "ID_RPL"
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

	public static class DomainmodelContext extends ParserRuleContext {
		public Token lang;
		public Token file;
		public Token HelperBlock;
		public List<Token> helperBlocks = new ArrayList<Token>();
		public Token TmplBlock;
		public List<Token> tmplBlocks = new ArrayList<Token>();
		public Token ModelBlock;
		public List<Token> modelBlocks = new ArrayList<Token>();
		public TerminalNode Lang() { return getToken(TLangParser.Lang, 0); }
		public TerminalNode File() { return getToken(TLangParser.File, 0); }
		public List<TerminalNode> HelperBlock() { return getTokens(TLangParser.HelperBlock); }
		public TerminalNode HelperBlock(int i) {
			return getToken(TLangParser.HelperBlock, i);
		}
		public List<TerminalNode> TmplBlock() { return getTokens(TLangParser.TmplBlock); }
		public TerminalNode TmplBlock(int i) {
			return getToken(TLangParser.TmplBlock, i);
		}
		public List<TerminalNode> ModelBlock() { return getTokens(TLangParser.ModelBlock); }
		public TerminalNode ModelBlock(int i) {
			return getToken(TLangParser.ModelBlock, i);
		}
		public DomainmodelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_domainmodel; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).enterDomainmodel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof TLangListener ) ((TLangListener)listener).exitDomainmodel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof TLangVisitor ) return ((TLangVisitor<? extends T>)visitor).visitDomainmodel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DomainmodelContext domainmodel() throws RecognitionException {
		DomainmodelContext _localctx = new DomainmodelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_domainmodel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(3);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==Lang) {
				{
				setState(2);
				((DomainmodelContext)_localctx).lang = match(Lang);
				}
			}

			setState(6);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==File) {
				{
				setState(5);
				((DomainmodelContext)_localctx).file = match(File);
				}
			}

			setState(11);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==HelperBlock) {
				{
				{
				setState(8);
				((DomainmodelContext)_localctx).HelperBlock = match(HelperBlock);
				((DomainmodelContext)_localctx).helperBlocks.add(((DomainmodelContext)_localctx).HelperBlock);
				}
				}
				setState(13);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TmplBlock) {
				{
				{
				setState(14);
				((DomainmodelContext)_localctx).TmplBlock = match(TmplBlock);
				((DomainmodelContext)_localctx).tmplBlocks.add(((DomainmodelContext)_localctx).TmplBlock);
				}
				}
				setState(19);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(23);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ModelBlock) {
				{
				{
				setState(20);
				((DomainmodelContext)_localctx).ModelBlock = match(ModelBlock);
				((DomainmodelContext)_localctx).modelBlocks.add(((DomainmodelContext)_localctx).ModelBlock);
				}
				}
				setState(25);
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\36\35\4\2\t\2\3\2"+
		"\5\2\6\n\2\3\2\5\2\t\n\2\3\2\7\2\f\n\2\f\2\16\2\17\13\2\3\2\7\2\22\n\2"+
		"\f\2\16\2\25\13\2\3\2\7\2\30\n\2\f\2\16\2\33\13\2\3\2\2\2\3\2\2\2\2 \2"+
		"\5\3\2\2\2\4\6\7\4\2\2\5\4\3\2\2\2\5\6\3\2\2\2\6\b\3\2\2\2\7\t\7\5\2\2"+
		"\b\7\3\2\2\2\b\t\3\2\2\2\t\r\3\2\2\2\n\f\7\6\2\2\13\n\3\2\2\2\f\17\3\2"+
		"\2\2\r\13\3\2\2\2\r\16\3\2\2\2\16\23\3\2\2\2\17\r\3\2\2\2\20\22\7\b\2"+
		"\2\21\20\3\2\2\2\22\25\3\2\2\2\23\21\3\2\2\2\23\24\3\2\2\2\24\31\3\2\2"+
		"\2\25\23\3\2\2\2\26\30\7\25\2\2\27\26\3\2\2\2\30\33\3\2\2\2\31\27\3\2"+
		"\2\2\31\32\3\2\2\2\32\3\3\2\2\2\33\31\3\2\2\2\7\5\b\r\23\31";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}