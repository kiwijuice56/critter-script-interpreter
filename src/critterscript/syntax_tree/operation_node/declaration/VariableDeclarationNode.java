package critterscript.syntax_tree.operation_node.declaration;

import critterscript.syntax_tree.operation_node.OperationNode;

public class VariableDeclarationNode extends OperationNode {
	private final String name;
	private final boolean isConst;
	private final boolean isGlobal;

	public VariableDeclarationNode(String name, boolean isConst, boolean isGlobal) {
		this.name = name;
		this.isConst = isConst;
		this.isGlobal = isGlobal;
	}

	public String toString() {
		return "new " + (isGlobal ? "global" : "instance") +(isConst ? " const: " : " var: ") + name;
	}
}
