package interpreter.syntax_tree;

import java.util.List;

public class OperationNode extends SyntaxNode {
	private final String operator;

	public OperationNode(String operator) {
		this.operator = operator;
	}

	public OperationNode(String operator, SyntaxNode child) {
		super(child);
		this.operator = operator;
	}

	public OperationNode(String operator, List<SyntaxNode> children) {
		super(children);
		this.operator = operator;
	}

	public String toString() {
		return "Operation: " + operator;
	}
}
