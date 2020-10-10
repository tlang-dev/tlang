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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		TEXT=18, ID=19, WS=20, STRING=21, NUMBER=22;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
			"TEXT", "ID", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'helper'", "'{'", "'}'", "'func'", "'if'", "'('", "')'", "'=='", 
			"'!='", "'<'", "'>'", "'<='", "'>='", "'for'", "'in'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "TEXT", "ID", "WS", "STRING", "NUMBER"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\30\u009d\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\22\3"+
		"\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24\5\24m\n\24\3\24\3\24"+
		"\3\24\5\24r\n\24\3\24\3\24\3\24\3\24\7\24x\n\24\f\24\16\24{\13\24\3\25"+
		"\6\25~\n\25\r\25\16\25\177\3\25\3\25\3\26\3\26\3\26\3\27\3\27\3\27\7\27"+
		"\u008a\n\27\f\27\16\27\u008d\13\27\3\27\3\27\3\30\6\30\u0092\n\30\r\30"+
		"\16\30\u0093\3\30\3\30\6\30\u0098\n\30\r\30\16\30\u0099\5\30\u009c\n\30"+
		"\3\u008b\2\31\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\2-\27/\30\3\2\6\6\2//C\\aac|"+
		"\7\2//\62;C\\aac|\5\2\13\f\17\17\"\"\4\2\f\f\17\17\2\u00a6\2\3\3\2\2\2"+
		"\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2"+
		"\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2"+
		"\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2"+
		"\2\2\'\3\2\2\2\2)\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\3\61\3\2\2\2\58\3\2\2"+
		"\2\7:\3\2\2\2\t<\3\2\2\2\13A\3\2\2\2\rD\3\2\2\2\17F\3\2\2\2\21H\3\2\2"+
		"\2\23K\3\2\2\2\25N\3\2\2\2\27P\3\2\2\2\31R\3\2\2\2\33U\3\2\2\2\35X\3\2"+
		"\2\2\37\\\3\2\2\2!_\3\2\2\2#a\3\2\2\2%c\3\2\2\2\'l\3\2\2\2)}\3\2\2\2+"+
		"\u0083\3\2\2\2-\u0086\3\2\2\2/\u0091\3\2\2\2\61\62\7j\2\2\62\63\7g\2\2"+
		"\63\64\7n\2\2\64\65\7r\2\2\65\66\7g\2\2\66\67\7t\2\2\67\4\3\2\2\289\7"+
		"}\2\29\6\3\2\2\2:;\7\177\2\2;\b\3\2\2\2<=\7h\2\2=>\7w\2\2>?\7p\2\2?@\7"+
		"e\2\2@\n\3\2\2\2AB\7k\2\2BC\7h\2\2C\f\3\2\2\2DE\7*\2\2E\16\3\2\2\2FG\7"+
		"+\2\2G\20\3\2\2\2HI\7?\2\2IJ\7?\2\2J\22\3\2\2\2KL\7#\2\2LM\7?\2\2M\24"+
		"\3\2\2\2NO\7>\2\2O\26\3\2\2\2PQ\7@\2\2Q\30\3\2\2\2RS\7>\2\2ST\7?\2\2T"+
		"\32\3\2\2\2UV\7@\2\2VW\7?\2\2W\34\3\2\2\2XY\7h\2\2YZ\7q\2\2Z[\7t\2\2["+
		"\36\3\2\2\2\\]\7k\2\2]^\7p\2\2^ \3\2\2\2_`\7]\2\2`\"\3\2\2\2ab\7_\2\2"+
		"b$\3\2\2\2cd\7$\2\2de\7$\2\2ef\7$\2\2fg\3\2\2\2gh\7$\2\2hi\7$\2\2ij\7"+
		"$\2\2j&\3\2\2\2km\7`\2\2lk\3\2\2\2lm\3\2\2\2mq\3\2\2\2nr\t\2\2\2op\7&"+
		"\2\2pr\7}\2\2qn\3\2\2\2qo\3\2\2\2ry\3\2\2\2sx\t\3\2\2tu\7&\2\2ux\7}\2"+
		"\2vx\7\177\2\2ws\3\2\2\2wt\3\2\2\2wv\3\2\2\2x{\3\2\2\2yw\3\2\2\2yz\3\2"+
		"\2\2z(\3\2\2\2{y\3\2\2\2|~\t\4\2\2}|\3\2\2\2~\177\3\2\2\2\177}\3\2\2\2"+
		"\177\u0080\3\2\2\2\u0080\u0081\3\2\2\2\u0081\u0082\b\25\2\2\u0082*\3\2"+
		"\2\2\u0083\u0084\7^\2\2\u0084\u0085\7$\2\2\u0085,\3\2\2\2\u0086\u008b"+
		"\7$\2\2\u0087\u008a\5+\26\2\u0088\u008a\n\5\2\2\u0089\u0087\3\2\2\2\u0089"+
		"\u0088\3\2\2\2\u008a\u008d\3\2\2\2\u008b\u008c\3\2\2\2\u008b\u0089\3\2"+
		"\2\2\u008c\u008e\3\2\2\2\u008d\u008b\3\2\2\2\u008e\u008f\7$\2\2\u008f"+
		".\3\2\2\2\u0090\u0092\4\62;\2\u0091\u0090\3\2\2\2\u0092\u0093\3\2\2\2"+
		"\u0093\u0091\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u009b\3\2\2\2\u0095\u0097"+
		"\7\60\2\2\u0096\u0098\4\62;\2\u0097\u0096\3\2\2\2\u0098\u0099\3\2\2\2"+
		"\u0099\u0097\3\2\2\2\u0099\u009a\3\2\2\2\u009a\u009c\3\2\2\2\u009b\u0095"+
		"\3\2\2\2\u009b\u009c\3\2\2\2\u009c\60\3\2\2\2\r\2lqwy\177\u0089\u008b"+
		"\u0093\u0099\u009b\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}