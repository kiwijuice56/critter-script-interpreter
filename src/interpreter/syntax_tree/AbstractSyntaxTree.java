package interpreter.syntax_tree;

import interpreter.SyntaxError;
import interpreter.tokenizer.Token;

import java.beans.Expression;
import java.util.InputMismatchException;
import java.util.List;

public class AbstractSyntaxTree {
	private final List<Token> tokens;
	private final SyntaxNode root;
	private int tokenIdx = 0;

	public AbstractSyntaxTree(List<Token> tokens) {
		this.tokens = tokens;
		root = expressionB();
	}

	private SyntaxNode expressionB() {
		SyntaxNode c = expressionC();
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.LOGICAL && tokens.get(tokenIdx).text().equals("or")) {
			String op = tokens.get(tokenIdx).text();
			tokenIdx++;
			SyntaxNode b = expressionB();
			return new OperationNode(op, List.of(b, c));
		}
		return c;
	}

	private SyntaxNode expressionC() {
		SyntaxNode d = expressionD();
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.LOGICAL && tokens.get(tokenIdx).text().equals("and")) {
			String op = tokens.get(tokenIdx).text();
			tokenIdx++;
			SyntaxNode c = expressionC();
			return new OperationNode(op, List.of(d, c));
		}
		return d;
	}

	private SyntaxNode expressionD() {
		SyntaxNode e = expressionE();
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.RELATIONAL) {
			String op = tokens.get(tokenIdx).text();
			tokenIdx++;
			SyntaxNode d = expressionD();
			return new OperationNode(op, List.of(e, d));
		}
		return e;
	}

	private SyntaxNode expressionE() {
		SyntaxNode f = expressionF();
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.ADDITIVE) {
			String op = tokens.get(tokenIdx).text();
			tokenIdx++;
			SyntaxNode e = expressionE();
			return new OperationNode(op, List.of(f, e));
		}
		return f;
	}

	private SyntaxNode expressionF() {
		SyntaxNode g = expressionG();
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.MULTIPLICATIVE) {
			String op = tokens.get(tokenIdx).text();
			tokenIdx++;
			SyntaxNode f = expressionF();
			return new OperationNode(op, List.of(g, f));
		}
		return g;
	}

	private SyntaxNode expressionG() {
		if (tokenIdx < tokens.size() && tokens.get(tokenIdx).type() == Token.Type.NEGATE) {
			tokenIdx++;
			SyntaxNode g = expressionG();
			return new OperationNode("not", g);
		}
		return group();
	}

	private SyntaxNode group() {
		if (tokens.get(tokenIdx).type() == Token.Type.GROUPING) {
			tokenIdx++;
			SyntaxNode e = expressionE();
			if (e == null)
				throw new SyntaxError("Expected expression inside of parentheses", tokens.get(tokenIdx).line(), tokens.get(tokenIdx).pos());
			if (tokens.get(tokenIdx).type() != Token.Type.GROUPING)
				throw new SyntaxError("Expected closing parentheses", tokens.get(tokenIdx).line(), tokens.get(tokenIdx).pos());
			tokenIdx++;
			return e;
		}
		return term();
	}

	private SyntaxNode term() {
		if (tokens.get(tokenIdx).type() == Token.Type.NUMBER)
			return new NumberNode(Double.parseDouble(tokens.get(tokenIdx++).text()));
		else if (tokens.get(tokenIdx).type() == Token.Type.STRING)
			return new StringNode(tokens.get(tokenIdx++).text());
		return null;
	}

	public String toString() {
		return root.treeString(0);
	}
}
