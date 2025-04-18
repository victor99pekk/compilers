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

}
