package critterscript.syntax_tree.term_node;

import critterscript.syntax_tree.SyntaxNode;

public class NumberNode extends SyntaxNode {
	private final double val;

	public NumberNode(double val) {
		this.val = val;
	}

	public String toString() {
		return "num: " + val;
	}
}
