// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\CommonLexer.g4 by ANTLR 4.8
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		TEXT=1, ID=2, WS=3, STRING=4, NUMBER=5;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"TEXT", "ID", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER"
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
			null, "TEXT", "ID", "WS", "STRING", "NUMBER"
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


	public CommonLexer(CharStream input) {
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\7I\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\3\5\3\31\n\3\3\3\3\3\3\3\5\3\36\n\3\3\3\3\3\3\3\3\3\7\3$\n\3\f\3\16"+
		"\3\'\13\3\3\4\6\4*\n\4\r\4\16\4+\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\7\6\66"+
		"\n\6\f\6\16\69\13\6\3\6\3\6\3\7\6\7>\n\7\r\7\16\7?\3\7\3\7\6\7D\n\7\r"+
		"\7\16\7E\5\7H\n\7\3\67\2\b\3\3\5\4\7\5\t\2\13\6\r\7\3\2\6\6\2//C\\aac"+
		"|\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2R\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\3\17\3\2\2\2\5\30\3\2\2"+
		"\2\7)\3\2\2\2\t/\3\2\2\2\13\62\3\2\2\2\r=\3\2\2\2\17\20\7$\2\2\20\21\7"+
		"$\2\2\21\22\7$\2\2\22\23\3\2\2\2\23\24\7$\2\2\24\25\7$\2\2\25\26\7$\2"+
		"\2\26\4\3\2\2\2\27\31\7`\2\2\30\27\3\2\2\2\30\31\3\2\2\2\31\35\3\2\2\2"+
		"\32\36\t\2\2\2\33\34\7&\2\2\34\36\7}\2\2\35\32\3\2\2\2\35\33\3\2\2\2\36"+
		"%\3\2\2\2\37$\t\3\2\2 !\7&\2\2!$\7}\2\2\"$\7\177\2\2#\37\3\2\2\2# \3\2"+
		"\2\2#\"\3\2\2\2$\'\3\2\2\2%#\3\2\2\2%&\3\2\2\2&\6\3\2\2\2\'%\3\2\2\2("+
		"*\t\4\2\2)(\3\2\2\2*+\3\2\2\2+)\3\2\2\2+,\3\2\2\2,-\3\2\2\2-.\b\4\2\2"+
		".\b\3\2\2\2/\60\7^\2\2\60\61\7$\2\2\61\n\3\2\2\2\62\67\7$\2\2\63\66\5"+
		"\t\5\2\64\66\n\5\2\2\65\63\3\2\2\2\65\64\3\2\2\2\669\3\2\2\2\678\3\2\2"+
		"\2\67\65\3\2\2\28:\3\2\2\29\67\3\2\2\2:;\7$\2\2;\f\3\2\2\2<>\4\62;\2="+
		"<\3\2\2\2>?\3\2\2\2?=\3\2\2\2?@\3\2\2\2@G\3\2\2\2AC\7\60\2\2BD\4\62;\2"+
		"CB\3\2\2\2DE\3\2\2\2EC\3\2\2\2EF\3\2\2\2FH\3\2\2\2GA\3\2\2\2GH\3\2\2\2"+
		"H\16\3\2\2\2\r\2\30\35#%+\65\67?EG\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}