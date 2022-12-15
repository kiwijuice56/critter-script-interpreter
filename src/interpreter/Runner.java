package interpreter;

import interpreter.syntax_tree.AbstractSyntaxTree;
import interpreter.tokenizer.Token;
import interpreter.tokenizer.Tokenizer;

import java.util.List;

public class Runner {
	public static void main(String[] args) {
		List<Token> t = Tokenizer.tokenize("""
    			var p = {1, 2, 3}
    			method fun(i, j)
    				var x = 2 and 3 - 1
    				var y = x * 2
    				x = y - p[0] / 2
    			
    			method awesome()
    				z = "yay"[2]
    				c = 2
    				
    			""");
		AbstractSyntaxTree a = new AbstractSyntaxTree(t);
		System.out.println(a);
	}
}
