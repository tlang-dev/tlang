// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLangModel.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLangModelLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, TEXT=14, ID=15, WS=16, STRING=17, 
		NUMBER=18;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "TEXT", "ID", "WS", "ESCAPED_QUOTE", 
			"STRING", "NUMBER"
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


	public TLangModelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TLangModel.g4"; }

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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\24\u0086\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\20\5\20V\n\20\3\20\3\20\3\20\5\20[\n\20\3\20\3\20\3\20\3\20\7"+
		"\20a\n\20\f\20\16\20d\13\20\3\21\6\21g\n\21\r\21\16\21h\3\21\3\21\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\7\23s\n\23\f\23\16\23v\13\23\3\23\3\23\3\24"+
		"\6\24{\n\24\r\24\16\24|\3\24\3\24\6\24\u0081\n\24\r\24\16\24\u0082\5\24"+
		"\u0085\n\24\3t\2\25\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\2%\23\'\24\3\2\6\6\2//C\\aac|\7\2//\62;"+
		"C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u008f\2\3\3\2\2\2\2\5\3\2\2"+
		"\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\3)\3"+
		"\2\2\2\5/\3\2\2\2\7\61\3\2\2\2\t\63\3\2\2\2\13\67\3\2\2\2\r9\3\2\2\2\17"+
		";\3\2\2\2\21=\3\2\2\2\23?\3\2\2\2\25A\3\2\2\2\27E\3\2\2\2\31G\3\2\2\2"+
		"\33I\3\2\2\2\35L\3\2\2\2\37U\3\2\2\2!f\3\2\2\2#l\3\2\2\2%o\3\2\2\2\'z"+
		"\3\2\2\2)*\7o\2\2*+\7q\2\2+,\7f\2\2,-\7g\2\2-.\7n\2\2.\4\3\2\2\2/\60\7"+
		"}\2\2\60\6\3\2\2\2\61\62\7\177\2\2\62\b\3\2\2\2\63\64\7n\2\2\64\65\7g"+
		"\2\2\65\66\7v\2\2\66\n\3\2\2\2\678\7*\2\28\f\3\2\2\29:\7.\2\2:\16\3\2"+
		"\2\2;<\7+\2\2<\20\3\2\2\2=>\7]\2\2>\22\3\2\2\2?@\7_\2\2@\24\3\2\2\2AB"+
		"\7u\2\2BC\7g\2\2CD\7v\2\2D\26\3\2\2\2EF\7>\2\2F\30\3\2\2\2GH\7@\2\2H\32"+
		"\3\2\2\2IJ\7/\2\2JK\7@\2\2K\34\3\2\2\2LM\7$\2\2MN\7$\2\2NO\7$\2\2OP\3"+
		"\2\2\2PQ\7$\2\2QR\7$\2\2RS\7$\2\2S\36\3\2\2\2TV\7`\2\2UT\3\2\2\2UV\3\2"+
		"\2\2VZ\3\2\2\2W[\t\2\2\2XY\7&\2\2Y[\7}\2\2ZW\3\2\2\2ZX\3\2\2\2[b\3\2\2"+
		"\2\\a\t\3\2\2]^\7&\2\2^a\7}\2\2_a\7\177\2\2`\\\3\2\2\2`]\3\2\2\2`_\3\2"+
		"\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2c \3\2\2\2db\3\2\2\2eg\t\4\2\2fe\3\2"+
		"\2\2gh\3\2\2\2hf\3\2\2\2hi\3\2\2\2ij\3\2\2\2jk\b\21\2\2k\"\3\2\2\2lm\7"+
		"^\2\2mn\7$\2\2n$\3\2\2\2ot\7$\2\2ps\5#\22\2qs\n\5\2\2rp\3\2\2\2rq\3\2"+
		"\2\2sv\3\2\2\2tu\3\2\2\2tr\3\2\2\2uw\3\2\2\2vt\3\2\2\2wx\7$\2\2x&\3\2"+
		"\2\2y{\4\62;\2zy\3\2\2\2{|\3\2\2\2|z\3\2\2\2|}\3\2\2\2}\u0084\3\2\2\2"+
		"~\u0080\7\60\2\2\177\u0081\4\62;\2\u0080\177\3\2\2\2\u0081\u0082\3\2\2"+
		"\2\u0082\u0080\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0085\3\2\2\2\u0084~"+
		"\3\2\2\2\u0084\u0085\3\2\2\2\u0085(\3\2\2\2\r\2UZ`bhrt|\u0082\u0084\3"+
		"\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}