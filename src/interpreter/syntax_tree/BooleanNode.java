package interpreter.syntax_tree;

public class BooleanNode extends SyntaxNode {
	private boolean val;

	public BooleanNode(boolean val) {
		this.val = val;
	}

	public String toString() {
		return "Boolean: " + val;
	}
}
