package interpreter.syntax_tree;

import interpreter.SyntaxError;
import interpreter.tokenizer.Token;

import java.util.*;

public class AbstractSyntaxTree {
	// Roots of method and variable trees
	private final List<MethodNode> methods;
	private final List<SyntaxNode> variableDeclarations;

	// Tokens to parse into a tree upon construction
	private final List<Token> tokens;
	private int tokenIdx = 0;

	public AbstractSyntaxTree(List<Token> tokens) {
		this.variableDeclarations = new ArrayList<>();
		this.methods = new ArrayList<>();

		this.tokens = tokens;
		script();
	}

	private void script() {
		while (getToken().type() != Token.Type.END_OF_FILE) {
			// Try to find either a method or loose variable declaration
			MethodNode methodNode = method();
			if (methodNode != null)
				methods.add(methodNode);

			SyntaxNode variableNode = variableDeclaration();
			if (variableNode != null)
				variableDeclarations.add(variableNode);

			// If neither are found, and we aren't at the end of the line, the user tried to put unexpected code
			if (getToken().type() == Token.Type.END_OF_LINE)
				consumeToken();
			else if (variableNode == null && methodNode == null)
				throw new SyntaxError("Expected method or variable declaration", getToken().line(), getToken().pos());
		}
	}

	private MethodNode method() {
		if (getToken().type() == Token.Type.KEY_WORD && getToken().text().equals("method")) {
			consumeToken();

			expect(Token.Type.IDENTIFIER);
			String methodName = consumeToken().text();

			expect(Token.Type.GROUPING, "(");
			consumeToken();

			List<String> parameters = new ArrayList<>();
			while (!(getToken().text().equals(")") && getToken().type() == Token.Type.GROUPING)) {
				expect(Token.Type.IDENTIFIER);
				parameters.add(consumeToken().text());
				if (getToken().type() == Token.Type.COMMA)
					consumeToken();
			}
			consumeToken();
			consumeToken();
			return new MethodNode(List.of(block()), methodName, parameters);
		}

		return null;
	}

	private SyntaxNode variableDeclaration() {
		if (getToken().type() == Token.Type.KEY_WORD && (getToken().text().equals("var") || getToken().text().equals("const"))) {
			boolean isConst = consumeToken().text().equals("const");
			expect(Token.Type.IDENTIFIER);
			String varName = consumeToken().text();
			expect(Token.Type.ASSIGN);
			consumeToken();
			return new OperationNode("<-", List.of(line(), new VariableDeclarationNode(varName, isConst)));
		}
		return null;
	}

	private SyntaxNode block() {
		// Keep adding lines with matching indent
		int startIndent = getToken().indent();
		SyntaxNode block = new BlockNode();
		while (getToken().indent() == startIndent) {
			SyntaxNode var = variableDeclaration();
			block.getChildren().add(var == null ? line() : var);
		}
		return block;
	}

	private SyntaxNode line() {
		SyntaxNode expr = expressionA();
		expect(Token.Type.END_OF_LINE);
		consumeToken();
		return expr;
	}

	private SyntaxNode expressionA() {
		SyntaxNode expr = expressionB();
		if (getToken().type() == Token.Type.ASSIGN) {
			consumeToken();
			return new OperationNode("=", List.of(expr,  expressionA()));
		}
		return expr;
	}

	private SyntaxNode expressionB() {
		SyntaxNode expr = expressionC();
		while (getToken().type() == Token.Type.LOGICAL && getToken().text().equals("or")) {
			consumeToken();
			expr = new OperationNode("or", List.of(expr, expressionC()));
		}
		return expr;
	}

	private SyntaxNode expressionC() {
		SyntaxNode expr = expressionD();
		while (getToken().type() == Token.Type.LOGICAL && getToken().text().equals("and")) {
			consumeToken();
			expr = new OperationNode("and", List.of(expr, expressionD()));
		}
		return expr;
	}

	private SyntaxNode expressionD() {
		SyntaxNode expr = expressionE();
		while (getToken().type() == Token.Type.RELATIONAL) {
			String op = getToken().text(); // Could be <=, >=, <, >, is, is not
			consumeToken();
			expr = new OperationNode(op, List.of(expr, expressionE()));
		}
		return expr;
	}

	private SyntaxNode expressionE() {
		SyntaxNode expr = expressionF();
		while (getToken().type() == Token.Type.ADDITIVE) {
			String op = getToken().text(); // Could be +, -
			consumeToken();
			expr = new OperationNode(op, List.of(expr, expressionF()));
		}
		return expr;
	}

	private SyntaxNode expressionF() {
		SyntaxNode expr = expressionG();
		while (getToken().type() == Token.Type.MULTIPLICATIVE) {
			String op = getToken().text(); // Could be *, %, /
			consumeToken();
			expr = new OperationNode(op, List.of(expr, expressionG()));
		}
		return expr;
	}

	private SyntaxNode expressionG() {
		if (getToken().type() == Token.Type.NEGATE) {
			consumeToken();
			return new OperationNode("not", expressionG());
		}
		return group();
	}

	private SyntaxNode group() {
		if (getToken().type() == Token.Type.GROUPING) {
			expect(Token.Type.GROUPING, "(");
			consumeToken();
			SyntaxNode expr = expressionA();
			expect(Token.Type.GROUPING, ")");
			consumeToken();
			return expr;
		}
		return term();
	}

	private SyntaxNode term() {
		switch (getToken().type()) {
			case NUMBER -> {
				return new NumberNode(Double.parseDouble(consumeToken().text()));
			} case BOOLEAN -> {
				return new BooleanNode(Boolean.parseBoolean(consumeToken().text()));
			} case ARRAY_DECLARATION -> {
				expect(Token.Type.ARRAY_DECLARATION, "{");
				SyntaxNode expr = new OperationNode("{}");

				// Check for an empty array {}
				consumeToken();
				if (getToken().text().equals("}") && getToken().type() == Token.Type.ARRAY_DECLARATION) {
					consumeToken();
					return expr;
				}

				while (true) {
					expr.getChildren().add(expressionA());

					// Either expect the end of the array and return or a comma for the next element
					if (getToken().text().equals("}") && getToken().type() == Token.Type.ARRAY_DECLARATION) {
						consumeToken();
						return expr;
					}
					expect(Token.Type.COMMA);

					consumeToken();
				}
			} case IDENTIFIER, STRING -> {
				SyntaxNode var = getToken().type() == Token.Type.IDENTIFIER ?
						new VariableNode(getToken().text()) : new StringNode(getToken().text());
				consumeToken();
				if (!(getToken().text().equals("[") && getToken().type() == Token.Type.INDEX))
					return var;

				// Create nested tree structure for multi-dimensional array access (ex: a[0][0])
				Queue<OperationNode> nestedIndex = new LinkedList<>();
				while (getToken().text().equals("[") && getToken().type() == Token.Type.INDEX) {
					consumeToken();
					nestedIndex.add(new OperationNode("[]", expressionA()));
					expect(Token.Type.INDEX, "]");
					consumeToken();
				}
				OperationNode last = nestedIndex.remove();
				last.getChildren().add(var);
				while (nestedIndex.size() >= 1) {
					OperationNode next = nestedIndex.poll();
					next.getChildren().add(last);
					last = next;
				}

				return last;
			}
		}
		throw new SyntaxError("Expected a term but found nothing", tokens.get(tokenIdx - 1).line(), tokens.get(tokenIdx - 1).pos());
	}

	private Token consumeToken() {
		Token token = getToken();
		tokenIdx = Math.min(tokens.size(), tokenIdx + 1);
		return token;
	}

	private Token getToken() {
		if (tokenIdx >= tokens.size())
			return new Token("EOF",
					tokens.get(tokenIdx - 1).line(),
					tokens.get(tokenIdx - 1).pos(),
					tokens.get(tokenIdx - 1).indent(), Token.Type.END_OF_FILE);
		else
			return tokens.get(tokenIdx);
	}

	private void expect(Token.Type expectedType) {
		if (getToken().type() != expectedType)
			throw new SyntaxError("Expected %s".formatted(expectedType.name()), getToken().line(), getToken().pos());
	}

	private void expect(Token.Type expectedType, String expectedText) {
		expect(expectedType);
		if (!getToken().text().equals(expectedText))
			throw new SyntaxError("Expected %s".formatted(expectedType.name()), getToken().line(), getToken().pos());
	}

	public String toString() {
		StringBuilder out = new StringBuilder();
		for (SyntaxNode v : variableDeclarations)
			out.append(v.treeString(0));

		for (MethodNode m : methods)
			out.append(m.treeString(0));

		return out.toString();
	}
}
