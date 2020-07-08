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
		T__9=10, T__10=11, T__11=12, T__12=13, TEXT=14, WS=15, STRING=16, NUMBER=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "TEXT", "WS", "ESCAPED_QUOTE", "STRING", 
			"NUMBER"
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
			null, null, "TEXT", "WS", "STRING", "NUMBER"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\23s\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\6"+
		"\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\r\3"+
		"\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\6\20T\n"+
		"\20\r\20\16\20U\3\20\3\20\3\21\3\21\3\21\3\22\3\22\3\22\7\22`\n\22\f\22"+
		"\16\22c\13\22\3\22\3\22\3\23\6\23h\n\23\r\23\16\23i\3\23\3\23\6\23n\n"+
		"\23\r\23\16\23o\5\23r\n\23\3a\2\24\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n"+
		"\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\2#\22%\23\3\2\4\5\2\13\f\17"+
		"\17\"\"\4\2\f\f\17\17\2w\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2"+
		"\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25"+
		"\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2"+
		"\2\2\2#\3\2\2\2\2%\3\2\2\2\3\'\3\2\2\2\5-\3\2\2\2\7/\3\2\2\2\t\61\3\2"+
		"\2\2\13\65\3\2\2\2\r\67\3\2\2\2\179\3\2\2\2\21;\3\2\2\2\23=\3\2\2\2\25"+
		"?\3\2\2\2\27C\3\2\2\2\31E\3\2\2\2\33G\3\2\2\2\35J\3\2\2\2\37S\3\2\2\2"+
		"!Y\3\2\2\2#\\\3\2\2\2%g\3\2\2\2\'(\7o\2\2()\7q\2\2)*\7f\2\2*+\7g\2\2+"+
		",\7n\2\2,\4\3\2\2\2-.\7}\2\2.\6\3\2\2\2/\60\7\177\2\2\60\b\3\2\2\2\61"+
		"\62\7n\2\2\62\63\7g\2\2\63\64\7v\2\2\64\n\3\2\2\2\65\66\7*\2\2\66\f\3"+
		"\2\2\2\678\7.\2\28\16\3\2\2\29:\7+\2\2:\20\3\2\2\2;<\7]\2\2<\22\3\2\2"+
		"\2=>\7_\2\2>\24\3\2\2\2?@\7u\2\2@A\7g\2\2AB\7v\2\2B\26\3\2\2\2CD\7>\2"+
		"\2D\30\3\2\2\2EF\7@\2\2F\32\3\2\2\2GH\7/\2\2HI\7@\2\2I\34\3\2\2\2JK\7"+
		"$\2\2KL\7$\2\2LM\7$\2\2MN\3\2\2\2NO\7$\2\2OP\7$\2\2PQ\7$\2\2Q\36\3\2\2"+
		"\2RT\t\2\2\2SR\3\2\2\2TU\3\2\2\2US\3\2\2\2UV\3\2\2\2VW\3\2\2\2WX\b\20"+
		"\2\2X \3\2\2\2YZ\7^\2\2Z[\7$\2\2[\"\3\2\2\2\\a\7$\2\2]`\5!\21\2^`\n\3"+
		"\2\2_]\3\2\2\2_^\3\2\2\2`c\3\2\2\2ab\3\2\2\2a_\3\2\2\2bd\3\2\2\2ca\3\2"+
		"\2\2de\7$\2\2e$\3\2\2\2fh\4\62;\2gf\3\2\2\2hi\3\2\2\2ig\3\2\2\2ij\3\2"+
		"\2\2jq\3\2\2\2km\7\60\2\2ln\4\62;\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3"+
		"\2\2\2pr\3\2\2\2qk\3\2\2\2qr\3\2\2\2r&\3\2\2\2\t\2U_aioq\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}