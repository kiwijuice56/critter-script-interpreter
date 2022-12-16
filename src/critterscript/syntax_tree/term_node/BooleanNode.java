package critterscript.syntax_tree.term_node;

import critterscript.syntax_tree.SyntaxNode;

public class BooleanNode extends SyntaxNode {
	private final boolean val;

	public BooleanNode(boolean val) {
		this.val = val;
	}

	public String toString() {
		return "bool: " + val;
	}
}
