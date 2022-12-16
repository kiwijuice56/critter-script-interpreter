package critterscript.tokenizer;

import critterscript.SyntaxError;

import java.util.List;

// Helper class to navigate a list of tokens
public class TokenParser {
    private int tokenIdx;
    private final List<Token> tokens;

    public TokenParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Returns the current token and increments tokenIdx
    public Token consume() {
        Token token = peek();
        tokenIdx = Math.min(tokens.size(), tokenIdx + 1);
        return token;
    }

    // Returns the current token without incrementing tokenIdx
    public Token peek() {
        if (tokenIdx >= tokens.size())
            return new Token("EOF",
                    tokens.get(tokenIdx - 1).line(),
                    tokens.get(tokenIdx - 1).pos(),
                    tokens.get(tokenIdx - 1).indent(), Token.Type.END_OF_FILE);
        else
            return tokens.get(tokenIdx);
    }

    // Throw an exception if a Token of the wrong type is found
    public void expect(Token.Type expectedType) {
        if (peek().type() != expectedType)
            throw new SyntaxError("Expected %s".formatted(expectedType.name()), peek().line(), peek().pos());
    }

    public void expect(Token.Type expectedType, String expectedText) {
        expect(expectedType);
        if (!peek().text().equals(expectedText))
            throw new SyntaxError("Expected %s".formatted(expectedType.name()), peek().line(), peek().pos());
    }
}
