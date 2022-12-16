package critterscript.interpreter;

import critterscript.simulation.Critter;
import critterscript.syntax_tree.AbstractSyntaxTree;

import java.util.List;
import java.util.Map;

public class Interpreter {
    private AbstractSyntaxTree ast;

    private CritterState globalState;
    private Map<Critter, CritterState> instanceState;

    public Interpreter(AbstractSyntaxTree ast) {
        this.ast = ast;
        this.globalState = new CritterState();
    }

    public void initializeCritter(Critter c) {
        instanceState.put(c, new CritterState());
        // TODO: Add instance variables from the AST to the state
    }

    public void removeCritter(Critter c) {
        instanceState.remove(c);
    }
}
