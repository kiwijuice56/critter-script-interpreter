package critterscript.syntax_tree.operation_node.declaration;

import critterscript.syntax_tree.SyntaxNode;

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
		return "new method: " + name + " " + parameters;
	}
}
