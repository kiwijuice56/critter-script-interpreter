package critterscript.syntax_tree.operation_node.assignment;

import critterscript.syntax_tree.SyntaxNode;
import critterscript.syntax_tree.operation_node.OperationNode;

import java.util.List;

public class AssignmentNode extends OperationNode {
	public AssignmentNode(List<SyntaxNode> children) {
		super(children);
	}

	public String toString() {
		return "=";
	}
}
