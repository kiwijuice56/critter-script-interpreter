package interpreter.tokenizer;

import interpreter.SyntaxError;

import java.util.*;

// Creates a list of tokens from raw text, allowing for easier parsing when creating the AST
public class Tokenizer {
	public static final String[] KEY_WORDS = new String[] {
			"if", "while", "else", "method", "return", "const", "var", "global", "pass"
	};

	public static List<Token> tokenize(String in) {
		in += "\n";
		Set<String> keyWordSet = new HashSet<>(Arrays.asList(KEY_WORDS));

		List<Token> tokens = new ArrayList<>();
		int pos = 0, indent = 0, line = 0;
		while (pos < in.length()) {
			int start = pos;
			char lookahead = in.charAt(pos);

			switch (lookahead) {
				case ' ' -> pos++;
				case '\n' -> {
					pos++;
					tokens.add(new Token("\\n", line, start, indent, Token.Type.END_OF_LINE));
					indent = 0;
					line++;
				} case '\t' -> {
					pos++;
					indent++;
				} case '+', '-' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.ADDITIVE));
				} case '*', '/', '%' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.MULTIPLICATIVE));
				} case '{', '}' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.ARRAY_DECLARATION));
				} case '[', ']' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.INDEX));
				} case '"' -> {
					StringBuilder strBuilder = new StringBuilder();
					do {
						strBuilder.append(in.charAt(pos++));
						if (pos >= in.length())
							throw new SyntaxError("Expected ending quote but found end of file. Did you forget to close a string?", line, pos);
					} while (in.charAt(pos) != '"');
					pos++;
					strBuilder.deleteCharAt(0);
					tokens.add(new Token(strBuilder.toString(), line, start, indent, Token.Type.STRING));
				} case '(', ')' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.GROUPING));
				} case ',' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.COMMA));
				} case ':' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.INDEX_RANGE));
				} case '=' -> {
					pos++;
					tokens.add(new Token(lookahead + "", line, start, indent, Token.Type.ASSIGN));
				} case '<', '>' -> {
					pos++;
					String comp = lookahead + "";
					if (pos < in.length() && in.charAt(pos) == '=')
						comp += in.charAt(pos++);
					tokens.add(new Token(comp, line, start, indent, Token.Type.RELATIONAL));
				} case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.' -> {
					StringBuilder num = new StringBuilder();
					while (pos < in.length() && (Character.isDigit(in.charAt(pos)) || in.charAt(pos) == '.'))
						num.append(in.charAt(pos++));
					if (pos < in.length() && Character.isAlphabetic(in.charAt(pos)))
						throw new SyntaxError("Expected a digit but found symbol %s".formatted(in.charAt(pos)), line, pos);
					tokens.add(new Token(num.toString(), line, start, indent, Token.Type.NUMBER));
				} default -> {
					if (!Character.isAlphabetic(lookahead) && in.charAt(pos) != '_')
						throw new SyntaxError("Unexpected symbol %s".formatted(in.charAt(pos)), line, pos);
					StringBuilder wordBuilder = new StringBuilder();
					while (pos < in.length() && (Character.isLetterOrDigit(in.charAt(pos)) || in.charAt(pos) == '_'))
						wordBuilder.append(in.charAt(pos++));

					String word = wordBuilder.toString();

					if (keyWordSet.contains(word)) {
						tokens.add(new Token(word, line, start, indent, Token.Type.KEY_WORD));
						continue;
					}

					switch (word) {
						case "is" -> {
							while (pos < in.length() && pos < start + 6)
								wordBuilder.append(in.charAt(pos++));
							if (wordBuilder.toString().equals("is not")) {
								tokens.add(new Token("is not", line, start, indent, Token.Type.RELATIONAL));
							} else {
								tokens.add(new Token("is", line, start, indent, Token.Type.RELATIONAL));
								pos = start + 2;
							}
						}
						case "true", "false" -> tokens.add(new Token(word, line, start, indent, Token.Type.BOOLEAN));
						case "and", "or" -> tokens.add(new Token(word, line, start, indent, Token.Type.LOGICAL));
						case "not" -> tokens.add(new Token(word, line, start, indent, Token.Type.NEGATE));
						default -> tokens.add(new Token(word, line, start, indent, Token.Type.IDENTIFIER));
					}
				}
			}
		}

		return tokens;
	}
}
