package critterscript.syntax_tree.operation_node.logical;

import critterscript.syntax_tree.SyntaxNode;
import critterscript.syntax_tree.operation_node.OperationNode;

public class NegateNode extends OperationNode {
	public NegateNode(SyntaxNode child) {
		super(child);
	}

	public String toString() {
		return "not";
	}
}
