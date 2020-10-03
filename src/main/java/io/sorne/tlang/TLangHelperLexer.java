// Generated from /home/joel/sorne_io/io.sorne.tlang/src/main/antlr/TLangHelper.g4 by ANTLR 4.8
package io.sorne.tlang;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TLangHelperLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		TEXT=10, ID=11, WS=12, STRING=13, NUMBER=14;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"TEXT", "ID", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'helper'", "'{'", "'}'", "'func'", "'if'", "'('", "')'", "'for'", 
			"'in'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, "TEXT", "ID", 
			"WS", "STRING", "NUMBER"
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


	public TLangHelperLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "TLangHelper.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\20y\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\7\3\7\3\b\3"+
		"\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\5\fI\n\f\3\f\3\f\3\f\5\fN\n\f\3\f\3\f\3\f\3\f\7\fT\n\f\f\f\16\fW"+
		"\13\f\3\r\6\rZ\n\r\r\r\16\r[\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\7\17"+
		"f\n\17\f\17\16\17i\13\17\3\17\3\17\3\20\6\20n\n\20\r\20\16\20o\3\20\3"+
		"\20\6\20t\n\20\r\20\16\20u\5\20x\n\20\3g\2\21\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\2\35\17\37\20\3\2\6\6\2//C\\aac|\7"+
		"\2//\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u0082\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\35\3\2\2\2\2\37\3\2\2\2\3!\3\2\2\2\5(\3\2\2\2\7*\3\2\2\2\t,\3\2\2\2\13"+
		"\61\3\2\2\2\r\64\3\2\2\2\17\66\3\2\2\2\218\3\2\2\2\23<\3\2\2\2\25?\3\2"+
		"\2\2\27H\3\2\2\2\31Y\3\2\2\2\33_\3\2\2\2\35b\3\2\2\2\37m\3\2\2\2!\"\7"+
		"j\2\2\"#\7g\2\2#$\7n\2\2$%\7r\2\2%&\7g\2\2&\'\7t\2\2\'\4\3\2\2\2()\7}"+
		"\2\2)\6\3\2\2\2*+\7\177\2\2+\b\3\2\2\2,-\7h\2\2-.\7w\2\2./\7p\2\2/\60"+
		"\7e\2\2\60\n\3\2\2\2\61\62\7k\2\2\62\63\7h\2\2\63\f\3\2\2\2\64\65\7*\2"+
		"\2\65\16\3\2\2\2\66\67\7+\2\2\67\20\3\2\2\289\7h\2\29:\7q\2\2:;\7t\2\2"+
		";\22\3\2\2\2<=\7k\2\2=>\7p\2\2>\24\3\2\2\2?@\7$\2\2@A\7$\2\2AB\7$\2\2"+
		"BC\3\2\2\2CD\7$\2\2DE\7$\2\2EF\7$\2\2F\26\3\2\2\2GI\7`\2\2HG\3\2\2\2H"+
		"I\3\2\2\2IM\3\2\2\2JN\t\2\2\2KL\7&\2\2LN\7}\2\2MJ\3\2\2\2MK\3\2\2\2NU"+
		"\3\2\2\2OT\t\3\2\2PQ\7&\2\2QT\7}\2\2RT\7\177\2\2SO\3\2\2\2SP\3\2\2\2S"+
		"R\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V\30\3\2\2\2WU\3\2\2\2XZ\t\4\2"+
		"\2YX\3\2\2\2Z[\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\]\3\2\2\2]^\b\r\2\2^\32\3"+
		"\2\2\2_`\7^\2\2`a\7$\2\2a\34\3\2\2\2bg\7$\2\2cf\5\33\16\2df\n\5\2\2ec"+
		"\3\2\2\2ed\3\2\2\2fi\3\2\2\2gh\3\2\2\2ge\3\2\2\2hj\3\2\2\2ig\3\2\2\2j"+
		"k\7$\2\2k\36\3\2\2\2ln\4\62;\2ml\3\2\2\2no\3\2\2\2om\3\2\2\2op\3\2\2\2"+
		"pw\3\2\2\2qs\7\60\2\2rt\4\62;\2sr\3\2\2\2tu\3\2\2\2us\3\2\2\2uv\3\2\2"+
		"\2vx\3\2\2\2wq\3\2\2\2wx\3\2\2\2x \3\2\2\2\r\2HMSU[egouw\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}