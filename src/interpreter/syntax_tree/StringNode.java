package interpreter.syntax_tree;

public class StringNode extends SyntaxNode {
	private String val;

	public StringNode(String val) {
		this.val = val;
	}

	public String toString() {
		return "String: " + val;
	}
}
