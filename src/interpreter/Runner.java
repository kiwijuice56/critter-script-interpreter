package interpreter;

import interpreter.syntax_tree.AbstractSyntaxTree;
import interpreter.tokenizer.Token;
import interpreter.tokenizer.TokenParser;
import interpreter.tokenizer.Tokenizer;

import java.util.List;

public class Runner {
	public static void main(String[] args) {
		List<Token> t = Tokenizer.tokenize("""
    			global var p = {1, 2, 3}
    			var x = 0
    			global const awesome = 213
    			method fun(i, j)
    				pass
    				return x + 3
    				var x = 2 and 3 - "yay"
    				var y = x * what(1, x(2) - yes(1)) - 2
    				x = y - p[0] / awesome(1, 2, 3)
    				print()
    				print(x + 1)
    				print(z - 1, 3)
    			
    			method awesome()
    				z = "yay"[2]
    				c = 2
    				
    			""");
		AbstractSyntaxTree a = new AbstractSyntaxTree(new TokenParser(t));
		System.out.println(a);
	}
}
