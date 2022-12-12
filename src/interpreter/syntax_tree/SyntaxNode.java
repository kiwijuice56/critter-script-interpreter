package interpreter.syntax_tree;

import java.util.ArrayList;
import java.util.List;

public class SyntaxNode {
	private List<SyntaxNode> children;

	public SyntaxNode(SyntaxNode child) {
		this.children = new ArrayList<>(List.of(child));
	}
	public SyntaxNode(List<SyntaxNode> children) {
		this.children = children;
	}

	public SyntaxNode() {
		this.children = new ArrayList<>();
	}

	public void process() {

	}

	public List<SyntaxNode> getChildren() {
		return children;
	}

	public String treeString(int indent) {
		StringBuilder out = new StringBuilder("\t".repeat(indent) + this + "\n");
		for (SyntaxNode child : children) {
			out.append(child.treeString(indent + 1));
		}
		return out.toString();
	}

	public String toString() {
		return "SyntaxNode";
	}
}
