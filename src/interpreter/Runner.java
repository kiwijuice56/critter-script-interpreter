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

		t = Tokenizer.tokenize("not ((x *  3)) + (1) * a[a[0]] + a[a][0]");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);

		t = Tokenizer.tokenize("\"yay\"[a + \"awesome\"] + {1, 2, 3} * {1, 2, 3, 4} + {}");
		a = new AbstractSyntaxTree(t);
		System.out.println(a);
	}
}
