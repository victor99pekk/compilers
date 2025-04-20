import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

import ir.IRFunction;
import ir.IRProgram;

public class ProgramCFG {
    
    private List<CFG> cfgs;

    public ProgramCFG(IRProgram program){
        this.cfgs = new ArrayList<CFG>();
        for (IRFunction function : program.functions){
            this.cfgs.add(CFG.mkCFG(function));
        }
    }

    public List<CFG> applyMarkSweep(){
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
