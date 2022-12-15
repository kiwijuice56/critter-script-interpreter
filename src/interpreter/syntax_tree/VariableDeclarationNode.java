package interpreter.syntax_tree;

public class VariableDeclarationNode extends SyntaxNode {
	private final String name;
	private final boolean isConst;

	public VariableDeclarationNode(String name, boolean isConst) {
		this.name = name;
		this.isConst = isConst;
	}

	public String toString() {
		return "New " + (isConst ? "Constant: " : "Variable: ") + name;
	}
}
