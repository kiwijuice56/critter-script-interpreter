package interpreter.syntax_tree;

import interpreter.SyntaxError;
import interpreter.tokenizer.Token;
import interpreter.tokenizer.TokenParser;

import java.util.*;

// TODO: Array splicing,
public class AbstractSyntaxTree {
	// Roots of method and variable trees
	// These are kept separate; we only need to initialize the variables once, while the methods are accessed often
	private final List<MethodDeclarationNode> methods;
	private final List<SyntaxNode> variableDeclarations;

	private final TokenParser tk;

	public AbstractSyntaxTree(TokenParser tokenParser) {
		this.variableDeclarations = new ArrayList<>();
		this.methods = new ArrayList<>();
		this.tk = tokenParser;
		script();
	}

	// Top level function to build script trees
	private void script() {
		while (tk.peek().type() != Token.Type.END_OF_FILE) {
			// Try to find either a method or loose variable declaration
			MethodDeclarationNode methodNode = method();
			if (methodNode != null)
				methods.add(methodNode);

			SyntaxNode variableNode = variableDeclaration();
			if (variableNode != null)
				variableDeclarations.add(variableNode);

			// If neither are found, and we aren't at the end of the line, the user tried to put unexpected code
			if (tk.peek().type() == Token.Type.END_OF_LINE)
				tk.consume();
			else if (variableNode == null && methodNode == null)
				throw new SyntaxError("Expected method or variable declaration", tk.peek().line(), tk.peek().pos());
		}
	}

	private MethodDeclarationNode method() {
		if (tk.peek().type() == Token.Type.KEY_WORD && tk.peek().text().equals("method")) {
			tk.consume();

			tk.expect(Token.Type.IDENTIFIER);
			String methodName = tk.consume().text();

			tk.expect(Token.Type.GROUPING, "(");
			tk.consume();

			List<String> parameters = new ArrayList<>();
			while (!(tk.peek().text().equals(")") && tk.peek().type() == Token.Type.GROUPING)) {
				tk.expect(Token.Type.IDENTIFIER);
				parameters.add(tk.consume().text());
				if (tk.peek().type() == Token.Type.COMMA)
					tk.consume();
			}
			tk.consume();
			tk.consume();
			return new MethodDeclarationNode(List.of(block()), methodName, parameters);
		}

		return null;
	}

	private SyntaxNode variableDeclaration() {
		if (tk.peek().type() == Token.Type.KEY_WORD && (
				tk.peek().text().equals("var") ||
				tk.peek().text().equals("const")) ||
				tk.peek().text().equals("global")) {
			boolean isGlobal = tk.peek().text().equals("global");
			if (isGlobal)
				tk.consume();
			boolean isConst = tk.consume().text().equals("const");
			tk.expect(Token.Type.IDENTIFIER);
			String varName = tk.consume().text();
			tk.expect(Token.Type.ASSIGN);
			tk.consume();
			return new OperationNode("<-", List.of(line(), new VariableDeclarationNode(varName, isConst, isGlobal)));
		}
		return null;
	}

	// Generic code block for methods, if statements, while loops, etc.
	private SyntaxNode block() {
		// Keep adding lines with matching indent
		int startIndent = tk.peek().indent();
		SyntaxNode block = new BlockNode();
		while (tk.peek().indent() == startIndent) {
			SyntaxNode returnStatement = returnStatement();

			if (returnStatement != null)
				block.getChildren().add(returnStatement);

			SyntaxNode passStatement = passStatement();

			if (passStatement != null)
				block.getChildren().add(passStatement);

			SyntaxNode variableDeclaration = variableDeclaration();
			if (variableDeclaration != null)
				block.getChildren().add(variableDeclaration);

			if (variableDeclaration == null && passStatement == null && returnStatement == null)
				block.getChildren().add(line());
		}
		return block;
	}

	private SyntaxNode line() {
		SyntaxNode expr = expressionA();
		tk.expect(Token.Type.END_OF_LINE);
		tk.consume();
		return expr;
	}

	private SyntaxNode returnStatement() {
		if (tk.peek().text().equals("return") && tk.peek().type() == Token.Type.KEY_WORD) {
			OperationNode expr = new OperationNode("return");
			tk.consume();
			expr.getChildren().add(expressionA());
			tk.consume();
			return expr;
		}
		return null;
	}

	private SyntaxNode passStatement() {
		if (tk.peek().text().equals("pass") && tk.peek().type() == Token.Type.KEY_WORD) {
			tk.consume();
			tk.consume();
			return new OperationNode("pass");
		}
		return null;
	}

	// The following expression methods create an order of operations in the AST structure
	// The higher the method is, the lower the precedence of that operator

	// Assignment
	private SyntaxNode expressionA() {
		SyntaxNode expr = expressionB();
		if (tk.peek().type() == Token.Type.ASSIGN) {
			tk.consume();
			return new OperationNode("=", List.of(expr, expressionA()));
		}
		return expr;
	}

	// Logical OR
	private SyntaxNode expressionB() {
		SyntaxNode expr = expressionC();
		while (tk.peek().type() == Token.Type.LOGICAL && tk.peek().text().equals("or")) {
			tk.consume();
			expr = new OperationNode("or", List.of(expr, expressionC()));
		}
		return expr;
	}

	// Logical AND
	private SyntaxNode expressionC() {
		SyntaxNode expr = expressionD();
		while (tk.peek().type() == Token.Type.LOGICAL && tk.peek().text().equals("and")) {
			tk.consume();
			expr = new OperationNode("and", List.of(expr, expressionD()));
		}
		return expr;
	}

	// Relational
	private SyntaxNode expressionD() {
		SyntaxNode expr = expressionE();
		while (tk.peek().type() == Token.Type.RELATIONAL) {
			String op = tk.consume().text(); // Could be <=, >=, <, >, is, is not
			expr = new OperationNode(op, List.of(expr, expressionE()));
		}
		return expr;
	}

	// Additive
	private SyntaxNode expressionE() {
		SyntaxNode expr = expressionF();
		while (tk.peek().type() == Token.Type.ADDITIVE) {
			String op = tk.consume().text(); // Could be +, -
			expr = new OperationNode(op, List.of(expr, expressionF()));
		}
		return expr;
	}

	// Multiplicative
	private SyntaxNode expressionF() {
		SyntaxNode expr = expressionG();
		while (tk.peek().type() == Token.Type.MULTIPLICATIVE) {
			String op = tk.consume().text(); // Could be *, %, /
			expr = new OperationNode(op, List.of(expr, expressionG()));
		}
		return expr;
	}

	// Unary NOT
	private SyntaxNode expressionG() {
		if (tk.peek().type() == Token.Type.NEGATE) {
			tk.consume();
			return new OperationNode("not", expressionG());
		}
		return group();
	}

	// Parentheses
	private SyntaxNode group() {
		if (tk.peek().type() == Token.Type.GROUPING) {
			tk.expect(Token.Type.GROUPING, "(");
			tk.consume();
			SyntaxNode expr = expressionA();
			tk.expect(Token.Type.GROUPING, ")");
			tk.consume();
			return expr;
		}
		return term();
	}

	// Terminals for expressions, including numbers, arrays, strings, booleans, and method calls
	// Terms can still call higher expressions if they are contained within array declarations or method arguments
	private SyntaxNode term() {
		switch (tk.peek().type()) {
			case NUMBER -> {
				return new NumberNode(Double.parseDouble(tk.consume().text()));
			} case BOOLEAN -> {
				return new BooleanNode(Boolean.parseBoolean(tk.consume().text()));
			} case ARRAY_DECLARATION -> {
				tk.expect(Token.Type.ARRAY_DECLARATION, "{");
				SyntaxNode expr = new OperationNode("{}");

				// Check for an empty array {}
				tk.consume();
				if (tk.peek().text().equals("}") && tk.peek().type() == Token.Type.ARRAY_DECLARATION) {
					tk.consume();
					return expr;
				}

				while (true) {
					expr.getChildren().add(expressionA());

					// Either expect the end of the array and return, or a comma for the next element
					if (tk.peek().text().equals("}") && tk.peek().type() == Token.Type.ARRAY_DECLARATION) {
						tk.consume();
						return expr;
					}
					tk.expect(Token.Type.COMMA);

					tk.consume();
				}
			} case STRING -> {
				StringNode stringNode = new StringNode(tk.consume().text());
				SyntaxNode indexedVariableNode = index(stringNode);
				return indexedVariableNode == null ? stringNode : indexedVariableNode;
			} case IDENTIFIER -> {
				VariableNode variableNode = new VariableNode(tk.consume().text());
				SyntaxNode indexedVariableNode = index(variableNode);
				if (indexedVariableNode != null)
					return indexedVariableNode;

				// Check if this is a method call rather than a variable reference
				if (!(tk.peek().type() == Token.Type.GROUPING && tk.peek().text().equals("(")))
					return variableNode;
				tk.consume();

				// Create the list of arguments (which could be any expression)
				List<SyntaxNode> arguments = new ArrayList<>();
				while (!(tk.peek().text().equals(")") && tk.peek().type() == Token.Type.GROUPING)) {
					arguments.add(expressionA());
					if (tk.peek().type() == Token.Type.COMMA)
						tk.consume();
				}
				tk.consume();
				return new MethodCallNode(arguments, variableNode.getName());
			}
		}
		throw new SyntaxError("Expected a term but found nothing", tk.peek().line(), tk.peek().pos());
	}

	// Helper method to detect array accessing, ex: cargill[0], "chinmay"[0:2]
	private SyntaxNode index(SyntaxNode array) {
		if (tk.peek().type() == Token.Type.INDEX) {
			tk.expect(Token.Type.INDEX, "[");

			// Create nested tree structure for multi-dimensional array access (ex: a[0][0])
			Queue<OperationNode> nestedIndex = new LinkedList<>();
			while (tk.peek().text().equals("[") && tk.peek().type() == Token.Type.INDEX) {
				tk.consume();
				nestedIndex.add(new OperationNode("[]", expressionA()));
				tk.expect(Token.Type.INDEX, "]");
				tk.consume();
			}

			OperationNode last = nestedIndex.remove();
			last.getChildren().add(array);
			while (nestedIndex.size() >= 1) {
				OperationNode next = nestedIndex.poll();
				next.getChildren().add(last);
				last = next;
			}

			return last;
		}
		return null;
	}

	public String toString() {
		StringBuilder out = new StringBuilder();
		for (SyntaxNode v : variableDeclarations)
			out.append(v.treeString(0));

		for (MethodDeclarationNode m : methods)
			out.append(m.treeString(0));

		return out.toString();
	}
}
