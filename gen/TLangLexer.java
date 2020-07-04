// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLangLexer extends Lexer {
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
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"STRING", "Lang", "File", "HelperBlock", "HelperFunc", "TmplBlock", "TmplPkg", 
			"TmplUse", "TmplImpl", "TmplFunc", "TmplCurrying", "TmplCurryingParam", 
			"TmplParam", "TmplType", "TmplGeneric", "TmplExpression", "TmplVal", 
			"TmplVar", "ModelBlock", "ModelNewEntity", "ModelValueType", "ModelTbl", 
			"ModelEntityAsAttribut", "ModelAttribut", "Text", "AnyID", "ID", "ID_RPL"
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


	public TLangLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TLang.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 3:
			HelperBlock_action((RuleContext)_localctx, actionIndex);
			break;
		case 5:
			TmplBlock_action((RuleContext)_localctx, actionIndex);
			break;
		case 11:
			TmplCurryingParam_action((RuleContext)_localctx, actionIndex);
			break;
		case 18:
			ModelBlock_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void HelperBlock_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:
			HelperBlock
			break;
		}
	}
	private void TmplBlock_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:
			TmplBlock
			break;
		}
	}
	private void TmplCurryingParam_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2:
			TmplCurryingParam
			break;
		}
	}
	private void ModelBlock_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:
			ModelBlock
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\36\u0190\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\2\7\2?\n\2\f\2"+
		"\16\2B\13\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5^\n\5\f\5\16\5a\13\5"+
		"\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\5\7v\n\7\3\7\7\7y\n\7\f\7\16\7|\13\7\3\7\7\7\177\n\7\f\7\16\7\u0082"+
		"\13\7\3\7\7\7\u0085\n\7\f\7\16\7\u0088\13\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\7\n\u00a6\n\n\f\n\16\n\u00a9\13\n\5\n\u00ab\n\n\3\n\3"+
		"\n\7\n\u00af\n\n\f\n\16\n\u00b2\13\n\3\n\7\n\u00b5\n\n\f\n\16\n\u00b8"+
		"\13\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00c3\n\13\f\13"+
		"\16\13\u00c6\13\13\3\13\3\13\3\13\3\13\7\13\u00cc\n\13\f\13\16\13\u00cf"+
		"\13\13\5\13\u00d1\n\13\3\13\3\13\7\13\u00d5\n\13\f\13\16\13\u00d8\13\13"+
		"\3\13\5\13\u00db\n\13\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\7\r\u00e5\n\r\f"+
		"\r\16\r\u00e8\13\r\5\r\u00ea\n\r\3\16\5\16\u00ed\n\16\3\16\3\16\3\16\5"+
		"\16\u00f2\n\16\3\17\3\17\3\17\3\17\3\17\5\17\u00f9\n\17\3\17\3\17\5\17"+
		"\u00fd\n\17\3\20\3\20\3\20\7\20\u0102\n\20\f\20\16\20\u0105\13\20\3\21"+
		"\3\21\5\21\u0109\n\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u0112\n"+
		"\22\3\22\3\22\5\22\u0116\n\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\5\23"+
		"\u011f\n\23\3\23\3\23\5\23\u0123\n\23\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\3\24\3\24\7\24\u012e\n\24\f\24\16\24\u0131\13\24\3\24\3\24\3\25\3"+
		"\25\3\25\3\25\3\25\7\25\u013a\n\25\f\25\16\25\u013d\13\25\3\25\3\25\5"+
		"\25\u0141\n\25\3\25\3\25\7\25\u0145\n\25\f\25\16\25\u0148\13\25\3\25\3"+
		"\25\3\26\3\26\3\26\5\26\u014f\n\26\3\27\5\27\u0152\n\27\3\27\3\27\3\27"+
		"\3\27\7\27\u0158\n\27\f\27\16\27\u015b\13\27\3\27\3\27\3\30\5\30\u0160"+
		"\n\30\3\30\3\30\3\31\5\31\u0165\n\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32"+
		"\3\32\3\32\3\32\3\32\3\33\3\33\5\33\u0174\n\33\3\34\5\34\u0177\n\34\3"+
		"\34\3\34\7\34\u017b\n\34\f\34\16\34\u017e\13\34\3\35\5\35\u0181\n\35\3"+
		"\35\3\35\3\35\5\35\u0186\n\35\3\35\3\35\3\35\3\35\7\35\u018c\n\35\f\35"+
		"\16\35\u018f\13\35\2\2\36\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25"+
		"\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32"+
		"\63\33\65\34\67\359\36\3\2\5\3\2$$\6\2//C\\aac|\7\2//\62;C\\aac|\2\u01bd"+
		"\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2"+
		"\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2"+
		"\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2"+
		"\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2"+
		"\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3"+
		"\2\2\2\5E\3\2\2\2\7L\3\2\2\2\tS\3\2\2\2\13d\3\2\2\2\rm\3\2\2\2\17\u008b"+
		"\3\2\2\2\21\u0091\3\2\2\2\23\u0097\3\2\2\2\25\u00bb\3\2\2\2\27\u00dc\3"+
		"\2\2\2\31\u00e0\3\2\2\2\33\u00ec\3\2\2\2\35\u00f3\3\2\2\2\37\u00fe\3\2"+
		"\2\2!\u0108\3\2\2\2#\u010a\3\2\2\2%\u0117\3\2\2\2\'\u0124\3\2\2\2)\u0134"+
		"\3\2\2\2+\u014e\3\2\2\2-\u0151\3\2\2\2/\u015f\3\2\2\2\61\u0164\3\2\2\2"+
		"\63\u0168\3\2\2\2\65\u0173\3\2\2\2\67\u0176\3\2\2\29\u0180\3\2\2\2;@\7"+
		"$\2\2<?\n\2\2\2=?\3\2\2\2><\3\2\2\2>=\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3"+
		"\2\2\2AC\3\2\2\2B@\3\2\2\2CD\7$\2\2D\4\3\2\2\2EF\7n\2\2FG\7c\2\2GH\7p"+
		"\2\2HI\7i\2\2IJ\3\2\2\2JK\5\3\2\2K\6\3\2\2\2LM\7h\2\2MN\7k\2\2NO\7n\2"+
		"\2OP\7g\2\2PQ\3\2\2\2QR\5\3\2\2R\b\3\2\2\2ST\b\5\2\2TU\7j\2\2UV\7g\2\2"+
		"VW\7n\2\2WX\7r\2\2XY\7g\2\2YZ\7t\2\2Z[\3\2\2\2[_\7}\2\2\\^\5\13\6\2]\\"+
		"\3\2\2\2^a\3\2\2\2_]\3\2\2\2_`\3\2\2\2`b\3\2\2\2a_\3\2\2\2bc\7\177\2\2"+
		"c\n\3\2\2\2de\7h\2\2ef\7w\2\2fg\7p\2\2gh\7e\2\2hi\3\2\2\2ij\5\65\33\2"+
		"jk\7}\2\2kl\7\177\2\2l\f\3\2\2\2mn\b\7\3\2no\7v\2\2op\7o\2\2pq\7r\2\2"+
		"qr\7n\2\2rs\3\2\2\2su\7}\2\2tv\5\17\b\2ut\3\2\2\2uv\3\2\2\2vz\3\2\2\2"+
		"wy\5\21\t\2xw\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{\u0080\3\2\2\2|z\3"+
		"\2\2\2}\177\5\23\n\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080"+
		"\u0081\3\2\2\2\u0081\u0086\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0085\5\25"+
		"\13\2\u0084\u0083\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084\3\2\2\2\u0086"+
		"\u0087\3\2\2\2\u0087\u0089\3\2\2\2\u0088\u0086\3\2\2\2\u0089\u008a\7\177"+
		"\2\2\u008a\16\3\2\2\2\u008b\u008c\7r\2\2\u008c\u008d\7m\2\2\u008d\u008e"+
		"\7i\2\2\u008e\u008f\3\2\2\2\u008f\u0090\5\3\2\2\u0090\20\3\2\2\2\u0091"+
		"\u0092\7w\2\2\u0092\u0093\7u\2\2\u0093\u0094\7g\2\2\u0094\u0095\3\2\2"+
		"\2\u0095\u0096\5\3\2\2\u0096\22\3\2\2\2\u0097\u0098\7k\2\2\u0098\u0099"+
		"\7o\2\2\u0099\u009a\7r\2\2\u009a\u009b\7n\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\u00aa\5\65\33\2\u009d\u009e\7h\2\2\u009e\u009f\7q\2\2\u009f\u00a0\7t"+
		"\2\2\u00a0\u00a1\3\2\2\2\u00a1\u00a2\5\65\33\2\u00a2\u00a7\3\2\2\2\u00a3"+
		"\u00a4\7.\2\2\u00a4\u00a6\5\65\33\2\u00a5\u00a3\3\2\2\2\u00a6\u00a9\3"+
		"\2\2\2\u00a7\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00ab\3\2\2\2\u00a9"+
		"\u00a7\3\2\2\2\u00aa\u009d\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ac\3\2"+
		"\2\2\u00ac\u00b0\7}\2\2\u00ad\u00af\5!\21\2\u00ae\u00ad\3\2\2\2\u00af"+
		"\u00b2\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b6\3\2"+
		"\2\2\u00b2\u00b0\3\2\2\2\u00b3\u00b5\5\25\13\2\u00b4\u00b3\3\2\2\2\u00b5"+
		"\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b6\u00b7\3\2\2\2\u00b7\u00b9\3\2"+
		"\2\2\u00b8\u00b6\3\2\2\2\u00b9\u00ba\7\177\2\2\u00ba\24\3\2\2\2\u00bb"+
		"\u00bc\7h\2\2\u00bc\u00bd\7w\2\2\u00bd\u00be\7p\2\2\u00be\u00bf\7e\2\2"+
		"\u00bf\u00c0\3\2\2\2\u00c0\u00c4\5\65\33\2\u00c1\u00c3\5\27\f\2\u00c2"+
		"\u00c1\3\2\2\2\u00c3\u00c6\3\2\2\2\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2"+
		"\2\2\u00c5\u00d0\3\2\2\2\u00c6\u00c4\3\2\2\2\u00c7\u00c8\7<\2\2\u00c8"+
		"\u00cd\5\35\17\2\u00c9\u00ca\7.\2\2\u00ca\u00cc\5\35\17\2\u00cb\u00c9"+
		"\3\2\2\2\u00cc\u00cf\3\2\2\2\u00cd\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce"+
		"\u00d1\3\2\2\2\u00cf\u00cd\3\2\2\2\u00d0\u00c7\3\2\2\2\u00d0\u00d1\3\2"+
		"\2\2\u00d1\u00da\3\2\2\2\u00d2\u00d6\7}\2\2\u00d3\u00d5\5!\21\2\u00d4"+
		"\u00d3\3\2\2\2\u00d5\u00d8\3\2\2\2\u00d6\u00d4\3\2\2\2\u00d6\u00d7\3\2"+
		"\2\2\u00d7\u00d9\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d9\u00db\7\177\2\2\u00da"+
		"\u00d2\3\2\2\2\u00da\u00db\3\2\2\2\u00db\26\3\2\2\2\u00dc\u00dd\7*\2\2"+
		"\u00dd\u00de\5\31\r\2\u00de\u00df\7+\2\2\u00df\30\3\2\2\2\u00e0\u00e9"+
		"\b\r\4\2\u00e1\u00e6\5\33\16\2\u00e2\u00e3\7.\2\2\u00e3\u00e5\5\33\16"+
		"\2\u00e4\u00e2\3\2\2\2\u00e5\u00e8\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e7"+
		"\3\2\2\2\u00e7\u00ea\3\2\2\2\u00e8\u00e6\3\2\2\2\u00e9\u00e1\3\2\2\2\u00e9"+
		"\u00ea\3\2\2\2\u00ea\32\3\2\2\2\u00eb\u00ed\5\67\34\2\u00ec\u00eb\3\2"+
		"\2\2\u00ec\u00ed\3\2\2\2\u00ed\u00ee\3\2\2\2\u00ee\u00f1\5\65\33\2\u00ef"+
		"\u00f0\7<\2\2\u00f0\u00f2\5\65\33\2\u00f1\u00ef\3\2\2\2\u00f1\u00f2\3"+
		"\2\2\2\u00f2\34\3\2\2\2\u00f3\u00f8\5\67\34\2\u00f4\u00f5\7>\2\2\u00f5"+
		"\u00f6\5\37\20\2\u00f6\u00f7\7@\2\2\u00f7\u00f9\3\2\2\2\u00f8\u00f4\3"+
		"\2\2\2\u00f8\u00f9\3\2\2\2\u00f9\u00fc\3\2\2\2\u00fa\u00fb\7]\2\2\u00fb"+
		"\u00fd\7_\2\2\u00fc\u00fa\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd\36\3\2\2\2"+
		"\u00fe\u0103\5\35\17\2\u00ff\u0100\7.\2\2\u0100\u0102\5\35\17\2\u0101"+
		"\u00ff\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103\u0104\3\2"+
		"\2\2\u0104 \3\2\2\2\u0105\u0103\3\2\2\2\u0106\u0109\5#\22\2\u0107\u0109"+
		"\5%\23\2\u0108\u0106\3\2\2\2\u0108\u0107\3\2\2\2\u0109\"\3\2\2\2\u010a"+
		"\u010b\7x\2\2\u010b\u010c\7c\2\2\u010c\u010d\7n\2\2\u010d\u010e\3\2\2"+
		"\2\u010e\u0111\5\65\33\2\u010f\u0110\7<\2\2\u0110\u0112\5\35\17\2\u0111"+
		"\u010f\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0115\3\2\2\2\u0113\u0114\7?"+
		"\2\2\u0114\u0116\5!\21\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116"+
		"$\3\2\2\2\u0117\u0118\7x\2\2\u0118\u0119\7c\2\2\u0119\u011a\7t\2\2\u011a"+
		"\u011b\3\2\2\2\u011b\u011e\5\65\33\2\u011c\u011d\7<\2\2\u011d\u011f\5"+
		"\35\17\2\u011e\u011c\3\2\2\2\u011e\u011f\3\2\2\2\u011f\u0122\3\2\2\2\u0120"+
		"\u0121\7?\2\2\u0121\u0123\5!\21\2\u0122\u0120\3\2\2\2\u0122\u0123\3\2"+
		"\2\2\u0123&\3\2\2\2\u0124\u0125\b\24\5\2\u0125\u0126\7o\2\2\u0126\u0127"+
		"\7q\2\2\u0127\u0128\7f\2\2\u0128\u0129\7g\2\2\u0129\u012a\7n\2\2\u012a"+
		"\u012b\3\2\2\2\u012b\u012f\7}\2\2\u012c\u012e\5)\25\2\u012d\u012c\3\2"+
		"\2\2\u012e\u0131\3\2\2\2\u012f\u012d\3\2\2\2\u012f\u0130\3\2\2\2\u0130"+
		"\u0132\3\2\2\2\u0131\u012f\3\2\2\2\u0132\u0133\7\177\2\2\u0133(\3\2\2"+
		"\2\u0134\u0140\5\67\34\2\u0135\u0136\7*\2\2\u0136\u013b\5\61\31\2\u0137"+
		"\u0138\7.\2\2\u0138\u013a\5\61\31\2\u0139\u0137\3\2\2\2\u013a\u013d\3"+
		"\2\2\2\u013b\u0139\3\2\2\2\u013b\u013c\3\2\2\2\u013c\u013e\3\2\2\2\u013d"+
		"\u013b\3\2\2\2\u013e\u013f\7+\2\2\u013f\u0141\3\2\2\2\u0140\u0135\3\2"+
		"\2\2\u0140\u0141\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0146\7}\2\2\u0143"+
		"\u0145\5+\26\2\u0144\u0143\3\2\2\2\u0145\u0148\3\2\2\2\u0146\u0144\3\2"+
		"\2\2\u0146\u0147\3\2\2\2\u0147\u0149\3\2\2\2\u0148\u0146\3\2\2\2\u0149"+
		"\u014a\7\177\2\2\u014a*\3\2\2\2\u014b\u014f\5\61\31\2\u014c\u014f\5/\30"+
		"\2\u014d\u014f\5-\27\2\u014e\u014b\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014d"+
		"\3\2\2\2\u014f,\3\2\2\2\u0150\u0152\5\67\34\2\u0151\u0150\3\2\2\2\u0151"+
		"\u0152\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\7]\2\2\u0154\u0159\5+\26"+
		"\2\u0155\u0156\7.\2\2\u0156\u0158\5+\26\2\u0157\u0155\3\2\2\2\u0158\u015b"+
		"\3\2\2\2\u0159\u0157\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u015c\3\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015c\u015d\7_\2\2\u015d.\3\2\2\2\u015e\u0160\5\67\34\2"+
		"\u015f\u015e\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2\2\2\u0161\u0162"+
		"\5)\25\2\u0162\60\3\2\2\2\u0163\u0165\5\67\34\2\u0164\u0163\3\2\2\2\u0164"+
		"\u0165\3\2\2\2\u0165\u0166\3\2\2\2\u0166\u0167\5\3\2\2\u0167\62\3\2\2"+
		"\2\u0168\u0169\7$\2\2\u0169\u016a\7$\2\2\u016a\u016b\7$\2\2\u016b\u016c"+
		"\3\2\2\2\u016c\u016d\5\63\32\2\u016d\u016e\7$\2\2\u016e\u016f\7$\2\2\u016f"+
		"\u0170\7$\2\2\u0170\64\3\2\2\2\u0171\u0174\5\67\34\2\u0172\u0174\59\35"+
		"\2\u0173\u0171\3\2\2\2\u0173\u0172\3\2\2\2\u0174\66\3\2\2\2\u0175\u0177"+
		"\7`\2\2\u0176\u0175\3\2\2\2\u0176\u0177\3\2\2\2\u0177\u0178\3\2\2\2\u0178"+
		"\u017c\t\3\2\2\u0179\u017b\t\4\2\2\u017a\u0179\3\2\2\2\u017b\u017e\3\2"+
		"\2\2\u017c\u017a\3\2\2\2\u017c\u017d\3\2\2\2\u017d8\3\2\2\2\u017e\u017c"+
		"\3\2\2\2\u017f\u0181\7`\2\2\u0180\u017f\3\2\2\2\u0180\u0181\3\2\2\2\u0181"+
		"\u0185\3\2\2\2\u0182\u0186\t\3\2\2\u0183\u0184\7&\2\2\u0184\u0186\7}\2"+
		"\2\u0185\u0182\3\2\2\2\u0185\u0183\3\2\2\2\u0186\u018d\3\2\2\2\u0187\u018c"+
		"\t\4\2\2\u0188\u0189\7&\2\2\u0189\u018c\7}\2\2\u018a\u018c\7\177\2\2\u018b"+
		"\u0187\3\2\2\2\u018b\u0188\3\2\2\2\u018b\u018a\3\2\2\2\u018c\u018f\3\2"+
		"\2\2\u018d\u018b\3\2\2\2\u018d\u018e\3\2\2\2\u018e:\3\2\2\2\u018f\u018d"+
		"\3\2\2\2/\2>@_uz\u0080\u0086\u00a7\u00aa\u00b0\u00b6\u00c4\u00cd\u00d0"+
		"\u00d6\u00da\u00e6\u00e9\u00ec\u00f1\u00f8\u00fc\u0103\u0108\u0111\u0115"+
		"\u011e\u0122\u012f\u013b\u0140\u0146\u014e\u0151\u0159\u015f\u0164\u0173"+
		"\u0176\u017c\u0180\u0185\u018b\u018d\6\3\5\2\3\7\3\3\r\4\3\24\5";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}