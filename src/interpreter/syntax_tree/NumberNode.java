package interpreter.syntax_tree;

public class NumberNode extends SyntaxNode {
	private final double val;

	public NumberNode(double val) {
		this.val = val;
	}

	public String toString() {
		return "Number: " + val;
	}
}
