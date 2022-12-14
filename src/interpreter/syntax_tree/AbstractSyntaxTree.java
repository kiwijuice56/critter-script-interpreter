package interpreter.syntax_tree;

import interpreter.SyntaxError;
import interpreter.tokenizer.Token;

import java.util.*;

public class AbstractSyntaxTree {
	private final List<Token> tokens;
	private final SyntaxNode root;
	private int tokenIdx = 0;

	public AbstractSyntaxTree(List<Token> tokens) {
		this.tokens = tokens;
		root = block();
	}

	private SyntaxNode script() {
		SyntaxNode expr = method();
		if (expr == null)
			expr = variableDeclaration();
	}

	private SyntaxNode method() {
		if (getToken().type() == Token.Type.KEY_WORD && getToken().text().equals("method")) {
			nextToken();
			if (getToken().type() != Token.Type.IDENTIFIER)
				throw new SyntaxError("Expected method name", getToken().line(), getToken().pos());
			String methodName = nextToken().text();
			if (!(getToken().text().equals("(") && getToken().type() == Token.Type.GROUPING))
				throw new SyntaxError("Expected '(' for parameters", getToken().line(), getToken().pos());
			while (nextToken().type() == Token.Type.IDENTIFIER) {
				if (nextToken())
			}
		}
	}

	private SyntaxNode variableDeclaration() {

	}

	private SyntaxNode block() {
		int startIndent = getToken().indent();
		SyntaxNode block = new BlockNode();
		while (getToken().indent() == startIndent)
			block.getChildren().add(line());
		return block;
	}

	private SyntaxNode line() {
		SyntaxNode expr = expressionA();
		if (getToken().type() != Token.Type.END_OF_LINE && getToken().type() != Token.Type.END_OF_FILE)
			throw new SyntaxError("Expected end of line", getToken().line(), getToken().pos());
		nextToken();
		return expr;
	}

	private SyntaxNode expressionA() {
		SyntaxNode expr = expressionB();
		if (getToken().type() == Token.Type.ASSIGN) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionA();
			return new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionB() {
		SyntaxNode expr = expressionC();
		while (getToken().type() == Token.Type.LOGICAL && getToken().text().equals("or")) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionC();
			expr = new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionC() {
		SyntaxNode expr = expressionD();
		while (getToken().type() == Token.Type.LOGICAL && getToken().text().equals("and")) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionD();
			expr = new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionD() {
		SyntaxNode expr = expressionE();
		while (getToken().type() == Token.Type.RELATIONAL) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionE();
			expr = new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionE() {
		SyntaxNode expr = expressionF();
		while (getToken().type() == Token.Type.ADDITIVE) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionF();
			expr = new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionF() {
		SyntaxNode expr = expressionG();
		while (getToken().type() == Token.Type.MULTIPLICATIVE) {
			String op = getToken().text();
			nextToken();
			SyntaxNode next = expressionG();
			expr = new OperationNode(op, List.of(expr, next));
		}
		return expr;
	}

	private SyntaxNode expressionG() {
		if (getToken().type() == Token.Type.NEGATE) {
			nextToken();
			SyntaxNode next = expressionG();
			return new OperationNode("not", next);
		}
		return group();
	}

	private SyntaxNode group() {
		if (getToken().type() == Token.Type.GROUPING) {
			nextToken();
			SyntaxNode expr = expressionA();
			if (getToken().type() != Token.Type.GROUPING)
				throw new SyntaxError("Expected closing ')'", getToken().line(), getToken().pos());
			nextToken();
			return expr;
		}
		return term();
	}

	private SyntaxNode term() {
		switch (getToken().type()) {
			case NUMBER -> {
				return new NumberNode(Double.parseDouble(nextToken().text()));
			} case BOOLEAN -> {
				return new BooleanNode(Boolean.parseBoolean(nextToken().text()));
			} case ARRAY_DECLARATION -> {
				if (getToken().text().equals("}"))
					throw new SyntaxError("Expected '{' instead of '}'", getToken().line(), getToken().pos());
				SyntaxNode expr = new OperationNode("{}");

				// Check for empty array '{}'
				nextToken();
				if (getToken().text().equals("}") && getToken().type() == Token.Type.ARRAY_DECLARATION) {
					nextToken();
					return expr;
				}

				while (true) {
					expr.getChildren().add(expressionA());

					if (getToken().text().equals("}") && getToken().type() == Token.Type.ARRAY_DECLARATION) {
						nextToken();
						return expr;
					}

					if (getToken().type() != Token.Type.COMMA)
						throw new SyntaxError("Expected comma after item declaration", getToken().line(), getToken().pos());

					nextToken();
				}
			} case IDENTIFIER, STRING -> {
				SyntaxNode var = getToken().type() == Token.Type.IDENTIFIER ?
						new VariableNode(getToken().text()) : new StringNode(getToken().text());
				nextToken();
				if (!(getToken().text().equals("[") && getToken().type() == Token.Type.INDEX))
					return var;

				Queue<OperationNode> nestedIndex = new LinkedList<>();
				while (getToken().text().equals("[") && getToken().type() == Token.Type.INDEX) {
					nextToken();
					SyntaxNode expr = expressionA();
					nestedIndex.add(new OperationNode("[]", new ArrayList<>(List.of(expr))));
					if (!(getToken().text().equals("]") && getToken().type() == Token.Type.INDEX))
						throw new SyntaxError("Expected closing ']' bracket", getToken().line(), getToken().pos());
					nextToken();
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

	private Token nextToken() {
		Token t = getToken();
		tokenIdx = Math.min(tokens.size(), tokenIdx + 1);
		return t;
	}

	private Token getToken() {
		if (tokenIdx >= tokens.size())
			return new Token("EOF", 0, 0, -1, Token.Type.END_OF_FILE);
		else
			return tokens.get(tokenIdx);
	}

	public String toString() {
		return root.treeString(0);
	}
}
