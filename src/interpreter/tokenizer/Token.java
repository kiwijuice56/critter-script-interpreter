package interpreter.tokenizer;

public record Token(String text, int line, int pos, int indent, Token.Type type) {
	public enum Type {
		END_OF_LINE,
		NUMBER,
		ADDITIVE,
		MULTIPLICATIVE,
		ARRAY_DEFINITION,
		ARRAY_ACCESS,
		ARRAY_SPLIT,
		GROUPING,
		RELATIONAL,
		COMMA,
		BOOLEAN,
		LOGICAL,
		NEGATE,
		IDENTIFIER,
		KEY_WORD,
		SET,
		STRING,
	}

	public String toString() {
		return type.toString() + "{'" + text + "', " + indent + "}";
	}
}
