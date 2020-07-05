// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/CommonLexer.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommonLexerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TEXT", "ANY_ID", "ID", "ID_RPL", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER"
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


	public CommonLexerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CommonLexer.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\2[\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\3\3\3\5\3\36\n\3\3\4\5\4!\n\4\3\4\3\4\7\4%\n\4\f\4"+
		"\16\4(\13\4\3\5\5\5+\n\5\3\5\3\5\3\5\5\5\60\n\5\3\5\3\5\3\5\3\5\7\5\66"+
		"\n\5\f\5\16\59\13\5\3\6\6\6<\n\6\r\6\16\6=\3\6\3\6\3\7\3\7\3\7\3\b\3\b"+
		"\3\b\7\bH\n\b\f\b\16\bK\13\b\3\b\3\b\3\t\6\tP\n\t\r\t\16\tQ\3\t\3\t\6"+
		"\tV\n\t\r\t\16\tW\5\tZ\n\t\3I\2\n\3\2\5\2\7\2\t\2\13\2\r\2\17\2\21\2\3"+
		"\2\6\6\2//C\\aac|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2"+
		"`\3\23\3\2\2\2\5\35\3\2\2\2\7 \3\2\2\2\t*\3\2\2\2\13;\3\2\2\2\rA\3\2\2"+
		"\2\17D\3\2\2\2\21O\3\2\2\2\23\24\7$\2\2\24\25\7$\2\2\25\26\7$\2\2\26\27"+
		"\3\2\2\2\27\30\7$\2\2\30\31\7$\2\2\31\32\7$\2\2\32\4\3\2\2\2\33\36\5\7"+
		"\4\2\34\36\5\t\5\2\35\33\3\2\2\2\35\34\3\2\2\2\36\6\3\2\2\2\37!\7`\2\2"+
		" \37\3\2\2\2 !\3\2\2\2!\"\3\2\2\2\"&\t\2\2\2#%\t\3\2\2$#\3\2\2\2%(\3\2"+
		"\2\2&$\3\2\2\2&\'\3\2\2\2\'\b\3\2\2\2(&\3\2\2\2)+\7`\2\2*)\3\2\2\2*+\3"+
		"\2\2\2+/\3\2\2\2,\60\t\2\2\2-.\7&\2\2.\60\7}\2\2/,\3\2\2\2/-\3\2\2\2\60"+
		"\67\3\2\2\2\61\66\t\3\2\2\62\63\7&\2\2\63\66\7}\2\2\64\66\7\177\2\2\65"+
		"\61\3\2\2\2\65\62\3\2\2\2\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678"+
		"\3\2\2\28\n\3\2\2\29\67\3\2\2\2:<\t\4\2\2;:\3\2\2\2<=\3\2\2\2=;\3\2\2"+
		"\2=>\3\2\2\2>?\3\2\2\2?@\b\6\2\2@\f\3\2\2\2AB\7^\2\2BC\7$\2\2C\16\3\2"+
		"\2\2DI\7$\2\2EH\5\r\7\2FH\n\5\2\2GE\3\2\2\2GF\3\2\2\2HK\3\2\2\2IJ\3\2"+
		"\2\2IG\3\2\2\2JL\3\2\2\2KI\3\2\2\2LM\7$\2\2M\20\3\2\2\2NP\4\62;\2ON\3"+
		"\2\2\2PQ\3\2\2\2QO\3\2\2\2QR\3\2\2\2RY\3\2\2\2SU\7\60\2\2TV\4\62;\2UT"+
		"\3\2\2\2VW\3\2\2\2WU\3\2\2\2WX\3\2\2\2XZ\3\2\2\2YS\3\2\2\2YZ\3\2\2\2Z"+
		"\22\3\2\2\2\20\2\35 &*/\65\67=GIQWY\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}