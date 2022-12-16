package critterscript.syntax_tree.operation_node;

import critterscript.syntax_tree.SyntaxNode;

import java.util.List;

public class OperationNode extends SyntaxNode {

	public OperationNode() {

	}

	public OperationNode(SyntaxNode child) {
		super(child);
	}

	public OperationNode(List<SyntaxNode> children) {
		super(children);
	}

	public String toString() {
		return "op";
	}
}
