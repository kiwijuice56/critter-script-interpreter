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

	private SyntaxNode block() {
		int startIndent = getToken().indent();
		SyntaxNode block = new BlockNode();
		while (getToken().indent() == startIndent)
			block.getChildren().add(line());
		return block;
	}

	private SyntaxNode line() {
		SyntaxNode expr = expressionA();
		if (getToken().type() != Token.Type.END_OF_LINE && getToken().type() != Token.Type.END_OF_FILE) {
			throw new SyntaxError("Expected end of line", getToken().line(), getToken().pos());
		}
		nextToken();
		return expr;
	}

	private SyntaxNode expressionA() {
		SyntaxNode expr = expressionB();
		if (getToken().type() == Token.Type.ASSIGN) {
			String op = getToken().text();
			nextToken();
			SyntaxNode a = expressionA();
			return new OperationNode(op, List.of(expr, a));
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
			SyntaxNode g = expressionG();
			return new OperationNode("not", g);
		}
		return group();
	}

	private SyntaxNode group() {
		if (getToken().type() == Token.Type.GROUPING) {
			nextToken();
			SyntaxNode e = expressionE();
			if (e == null)
				throw new SyntaxError("Expected expression inside of parentheses", getToken().line(), getToken().pos());
			if (getToken().type() != Token.Type.GROUPING)
				throw new SyntaxError("Expected closing parentheses", getToken().line(), getToken().pos());
			nextToken();
			return e;
		}
		return term();
	}

	private SyntaxNode term() {
		switch (getToken().type()) {
			case NUMBER -> {
				return new NumberNode(Double.parseDouble(nextToken().text()));
			} case STRING -> {
				return new StringNode(nextToken().text());
			} case BOOLEAN -> {
				return new BooleanNode(Boolean.parseBoolean(nextToken().text()));
			} case IDENTIFIER -> {
				SyntaxNode v = new VariableNode(getToken().text());
				nextToken();
				if (getToken().type() != Token.Type.ARRAY_ACCESS)
					return v;

				Queue<OperationNode> ops = new LinkedList<>();
				while (getToken().type() == Token.Type.ARRAY_ACCESS) {
					nextToken();
					SyntaxNode expr = expressionA();
					ops.add(new OperationNode("[]", new ArrayList<>(List.of(expr))));
					if (getToken().type() != Token.Type.ARRAY_ACCESS)
						throw new SyntaxError("Expected closing brackets", getToken().line(), getToken().pos());
					nextToken();
				}
				OperationNode last = ops.remove();
				last.getChildren().add(v);
				while (ops.size() >= 1) {
					OperationNode next = ops.poll();
					next.getChildren().add(last);
					last = next;
				}

				return last;
			}
		}
		throw new SyntaxError("Expected term but found nothing", tokens.get(tokenIdx - 1).line(), tokens.get(tokenIdx - 1).pos());
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
