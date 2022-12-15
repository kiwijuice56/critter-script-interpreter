package interpreter.syntax_tree;

import java.util.List;

public class MethodDeclarationNode extends SyntaxNode {
	private final String name;
	private final List<String> parameters;

	public MethodDeclarationNode(List<SyntaxNode> children, String name, List<String> parameters) {
		super(children);
		this.name = name;
		this.parameters = parameters;
	}

	public String toString() {
		return "Method Declaration: " + name + " " + parameters;
	}
}
