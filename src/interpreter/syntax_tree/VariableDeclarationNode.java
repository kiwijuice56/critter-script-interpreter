package interpreter.syntax_tree;

public class VariableDeclarationNode extends SyntaxNode {
	private final String name;
	private final boolean isConst;
	private final boolean isGlobal;

	public VariableDeclarationNode(String name, boolean isConst, boolean isGlobal) {
		this.name = name;
		this.isConst = isConst;
		this.isGlobal = isGlobal;
	}

	public String toString() {
		return "New " + (isGlobal ? "Global" : "Instance") +(isConst ? " Constant: " : " Variable: ") + name;
	}
}
