package interpreter.syntax_tree;

public class VariableNode extends SyntaxNode {
	private String name;

	public VariableNode(String name) {
		this.name = name;
	}

	public String toString() {
		return "Variable: " + name;
	}
}
