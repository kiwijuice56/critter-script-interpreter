package critterscript.syntax_tree.term_node;

import critterscript.syntax_tree.SyntaxNode;

public class VariableNode extends SyntaxNode {
	private final String name;

	public VariableNode(String name) {
		this.name = name;
	}

	public String toString() {
		return "var: " + name;
	}

	public String getName() {
		return name;
	}
}
