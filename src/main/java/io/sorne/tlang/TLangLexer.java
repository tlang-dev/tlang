// Generated from C:/Users/joel/sorne_io/io.sorne.tlang/src/main/antlr\TLang.g4 by ANTLR 4.8
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
public class TLangLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, WS=3, STRING=4, NUMBER=5, TEXT=6, ANY_ID=7, ID=8, ID_RPL=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "WS", "ESCAPED_QUOTE", "STRING", "NUMBER", "TEXT", "ANY_ID", 
			"ID", "ID_RPL"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'lang'", "'file'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, "WS", "STRING", "NUMBER", "TEXT", "ANY_ID", "ID", "ID_RPL"
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

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\13i\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\4\6\4#\n\4\r\4\16\4$\3\4"+
		"\3\4\3\5\3\5\3\5\3\6\3\6\3\6\7\6/\n\6\f\6\16\6\62\13\6\3\6\3\6\3\7\6\7"+
		"\67\n\7\r\7\16\78\3\7\3\7\6\7=\n\7\r\7\16\7>\5\7A\n\7\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\3\b\3\b\3\t\3\t\5\tM\n\t\3\n\5\nP\n\n\3\n\3\n\7\nT\n\n\f\n\16"+
		"\nW\13\n\3\13\5\13Z\n\13\3\13\3\13\3\13\5\13_\n\13\3\13\3\13\3\13\3\13"+
		"\7\13e\n\13\f\13\16\13h\13\13\3\60\2\f\3\3\5\4\7\5\t\2\13\6\r\7\17\b\21"+
		"\t\23\n\25\13\3\2\6\5\2\13\f\17\17\"\"\4\2\f\f\17\17\6\2//C\\aac|\7\2"+
		"//\62;C\\aac|\2u\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\13\3\2\2\2\2\r"+
		"\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\3\27\3\2"+
		"\2\2\5\34\3\2\2\2\7\"\3\2\2\2\t(\3\2\2\2\13+\3\2\2\2\r\66\3\2\2\2\17B"+
		"\3\2\2\2\21L\3\2\2\2\23O\3\2\2\2\25Y\3\2\2\2\27\30\7n\2\2\30\31\7c\2\2"+
		"\31\32\7p\2\2\32\33\7i\2\2\33\4\3\2\2\2\34\35\7h\2\2\35\36\7k\2\2\36\37"+
		"\7n\2\2\37 \7g\2\2 \6\3\2\2\2!#\t\2\2\2\"!\3\2\2\2#$\3\2\2\2$\"\3\2\2"+
		"\2$%\3\2\2\2%&\3\2\2\2&\'\b\4\2\2\'\b\3\2\2\2()\7^\2\2)*\7$\2\2*\n\3\2"+
		"\2\2+\60\7$\2\2,/\5\t\5\2-/\n\3\2\2.,\3\2\2\2.-\3\2\2\2/\62\3\2\2\2\60"+
		"\61\3\2\2\2\60.\3\2\2\2\61\63\3\2\2\2\62\60\3\2\2\2\63\64\7$\2\2\64\f"+
		"\3\2\2\2\65\67\4\62;\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\2"+
		"9@\3\2\2\2:<\7\60\2\2;=\4\62;\2<;\3\2\2\2=>\3\2\2\2><\3\2\2\2>?\3\2\2"+
		"\2?A\3\2\2\2@:\3\2\2\2@A\3\2\2\2A\16\3\2\2\2BC\7$\2\2CD\7$\2\2DE\7$\2"+
		"\2EF\3\2\2\2FG\7$\2\2GH\7$\2\2HI\7$\2\2I\20\3\2\2\2JM\5\23\n\2KM\5\25"+
		"\13\2LJ\3\2\2\2LK\3\2\2\2M\22\3\2\2\2NP\7`\2\2ON\3\2\2\2OP\3\2\2\2PQ\3"+
		"\2\2\2QU\t\4\2\2RT\t\5\2\2SR\3\2\2\2TW\3\2\2\2US\3\2\2\2UV\3\2\2\2V\24"+
		"\3\2\2\2WU\3\2\2\2XZ\7`\2\2YX\3\2\2\2YZ\3\2\2\2Z^\3\2\2\2[_\t\4\2\2\\"+
		"]\7&\2\2]_\7}\2\2^[\3\2\2\2^\\\3\2\2\2_f\3\2\2\2`e\t\5\2\2ab\7&\2\2be"+
		"\7}\2\2ce\7\177\2\2d`\3\2\2\2da\3\2\2\2dc\3\2\2\2eh\3\2\2\2fd\3\2\2\2"+
		"fg\3\2\2\2g\26\3\2\2\2hf\3\2\2\2\20\2$.\608>@LOUY^df\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}