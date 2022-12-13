package interpreter.syntax_tree;

import java.util.List;

public class BlockNode extends SyntaxNode {
	public BlockNode() {
		super();
	}

	public BlockNode(String operator, SyntaxNode child) {
		super(child);
	}

	public BlockNode(List<SyntaxNode> children) {
		super(children);
	}

	public String toString() {
		return "Block: ";
	}
}
