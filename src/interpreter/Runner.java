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

		t = Tokenizer.tokenize("true or false\n 1 * 2 + 1\n\"wow\"");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);

		t = Tokenizer.tokenize("a[0] + 2");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);

		t = Tokenizer.tokenize("a[0][1] + b[0][1][2] + asdn * 2");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);
	}
}
