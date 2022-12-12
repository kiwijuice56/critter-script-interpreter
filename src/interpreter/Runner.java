package interpreter;

import interpreter.syntax_tree.AbstractSyntaxTree;
import interpreter.tokenizer.Token;
import interpreter.tokenizer.Tokenizer;

import java.util.List;

public class Runner {
	public static void main(String[] args) {
		List<Token> t = Tokenizer.tokenize("2 * (2 + 3) + (5 - 2) + \"wow123\" + \"2\" + \"\"");
		AbstractSyntaxTree a = new AbstractSyntaxTree(t);
		System.out.println(a);

		t = Tokenizer.tokenize("(2 +3 * 2 / 123)");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);
	}
}
