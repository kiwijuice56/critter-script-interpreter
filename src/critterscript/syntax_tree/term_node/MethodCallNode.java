package critterscript.syntax_tree.term_node;

import critterscript.syntax_tree.SyntaxNode;

import java.util.List;

public class MethodCallNode extends SyntaxNode {
	private final String name;

	public MethodCallNode(List<SyntaxNode> children, String name) {
		super(children);
		this.name = name;
	}

	public String toString() {
		return "method call: " + name;
	}
}
