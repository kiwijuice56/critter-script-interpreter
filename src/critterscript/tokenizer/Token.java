package critterscript.tokenizer;

// The building blocks of CritterScript code
public record Token(String text, int line, int pos, int indent, Token.Type type) {
	public enum Type {
		ADDITIVE,
		ARRAY_DECLARATION, // Tokens for '{' are necessary because expressions can be used to initialize arrays
		ASSIGN,
		BOOLEAN,
		COMMA,
		END_OF_FILE,
		END_OF_LINE,
		GROUPING,
		IDENTIFIER,
		INDEX,
		INDEX_RANGE,
		KEY_WORD,
		LOGICAL,
		MULTIPLICATIVE,
		NEGATE,
		NUMBER,
		RELATIONAL,
		STRING, // Strings can be considered as a while because they are raw text
	}

	public String toString() {
		return type.toString() + "{'" + text + "', " + indent + "}";
	}
}
