package critterscript.syntax_tree.term_node;

import critterscript.syntax_tree.SyntaxNode;

public class StringNode extends SyntaxNode {
	private final String val;

	public StringNode(String val) {
		this.val = val;
	}

	public String toString() {
		return "str: " + val;
	}
}
