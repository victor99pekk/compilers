import java.util.HashSet;
import java.util.Set;

import ir.IRFunction;
import ir.IRProgram;

public class ProgramCFG {
    
    private Set<CFG> cfgs;

    public ProgramCFG(IRProgram program){
        this.cfgs = new HashSet<>();
        for (IRFunction function : program.functions){
            this.cfgs.add(CFG.mkCFG(function));
        }
    }

    public Set<CFG> applyMarkSweep(){
        for (CFG cfg : this.cfgs){
            MarkSweep markSweep = new MarkSweep();
            markSweep.applyMarkSweep(cfg);
        }
        return this.cfgs;
    }

    public IRProgram convertToIRProgram(){
        IRProgram program = new IRProgram();
        for (CFG cfg : cfgs) {
            program.functions.add(CFG.mkFunction(cfg));
        }
        return program;
    }

    // public ProgramCFG compileWithOptimization(IRProgram program){
    //     return new ProgramCFG(program).applyMarkSweep().convertToIRProgram();
    // }
}
