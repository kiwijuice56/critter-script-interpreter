package critterscript.syntax_tree.operation_node.array;

import critterscript.syntax_tree.SyntaxNode;
import critterscript.syntax_tree.operation_node.OperationNode;

public class ArrayIndexNode extends OperationNode {
	public ArrayIndexNode(SyntaxNode child) {
		super(child);
	}

	public String toString() {
		return "[ index ]";
	}
}
