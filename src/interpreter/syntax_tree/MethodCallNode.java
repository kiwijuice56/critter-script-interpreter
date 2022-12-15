package interpreter.syntax_tree;

import java.util.List;

public class MethodCallNode extends SyntaxNode {
	private final String name;

	public MethodCallNode(List<SyntaxNode> children, String name) {
		super(children);
		this.name = name;
	}

	public String toString() {
		return "Method Call: " + name;
	}
}
