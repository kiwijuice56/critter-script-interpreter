package interpreter.tokenizer;

public record Token(String text, int line, int pos, int indent, Token.Type type) {
	public enum Type {
		ADDITIVE,
		ARRAY_ACCESS,
		ARRAY_DEFINITION,
		ARRAY_SPLIT,
		ASSIGN,
		BOOLEAN,
		COMMA,
		END_OF_FILE,
		END_OF_LINE,
		GROUPING,
		IDENTIFIER,
		KEY_WORD,
		LOGICAL,
		MULTIPLICATIVE,
		NEGATE,
		NUMBER,
		RELATIONAL,
		STRING,
	}

	public String toString() {
		return type.toString() + "{'" + text + "', " + indent + "}";
	}
}
