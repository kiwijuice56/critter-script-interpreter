package critterscript.syntax_tree.operation_node.logical;

import critterscript.syntax_tree.SyntaxNode;
import critterscript.syntax_tree.operation_node.OperationNode;

import java.util.List;

public class OrNode extends OperationNode {
	public OrNode(List<SyntaxNode> children) {
		super(children);
	}

	public String toString() {
		return "or";
	}
}
