package interpreter.syntax_tree;

import java.util.List;

public class MethodNode extends SyntaxNode {
	private final String name;
	private final List<String> parameters;

	public MethodNode(List<SyntaxNode> children, String name, List<String> parameters) {
		super(children);
		this.name = name;
		this.parameters = parameters;
	}

	public String toString() {
		return "Method: " + name + " " + parameters;
	}
}
