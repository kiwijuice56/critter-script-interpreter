package interpreter;

public class SyntaxError extends RuntimeException  {
	public SyntaxError(String errorMessage, int line, int pos) {
		super(errorMessage + " (%d, %d)".formatted(line, pos));
	}
}
