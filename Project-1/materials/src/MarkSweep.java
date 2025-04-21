import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import ir.IRInstruction;
import ir.operand.IROperand;

public class MarkSweep {
    
    private Set<IRInstruction> marked;
    private Set<IRInstruction> worklist;

    // represents the operands being read from in a critical operation
    // OpCode enum exists because array_load instructions need to be handled specially
    private static class CriticalOperands {
        public enum OpCode {
            ARRAY_LOAD,
            NON_ARRAY_LOAD;
        }
        public OpCode opCode;
        public List<IROperand> critOps;
    }

    public MarkSweep(){
        this.marked = new HashSet<>();
        this.worklist = new HashSet<>();
    }

    private boolean isDefinition(IRInstruction instr) {
        switch (instr.opCode) {
            case ASSIGN:
            case ADD:
            case SUB:
            case MULT:
            case DIV:
            case AND:
            case OR:
            case ARRAY_STORE:
            case ARRAY_LOAD:
            case CALLR:
                return true;
            default:
                return false;
        }
    }

    private boolean isCritical(IRInstruction instr) {
        switch (instr.opCode) {
            case GOTO:
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ:
            case RETURN:
            case CALL:
            case CALLR:
            case LABEL:
                return true;
            default:
                return false;
        }
    }

    // for a instruction marked as critical, get it's (input) operands
    private CriticalOperands criticalOperands(IRInstruction instr) {
        CriticalOperands ops = new CriticalOperands();
        
        switch (instr.opCode) {
            case ASSIGN:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                if (instr.operands.length == 2) { // variable assign (Tiger IR page 3)
                    ops.critOps.add(instr.operands[1]);
                } else { // array assign (Tiger IR page 5)
                    ops.critOps.add(instr.operands[2]);
                }
                break;
            case ADD:
            case SUB:
            case MULT:
            case DIV:
            case AND:
            case OR:
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                ops.critOps.add(instr.operands[1]);
                ops.critOps.add(instr.operands[2]);
                break;
            case RETURN:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                ops.critOps.add(instr.operands[0]);
                break;
            case CALL:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                for (int i = 1; i < instr.operands.length; i++) {
                    ops.critOps.add(instr.operands[i]);
                }
                break;
            case CALLR:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                for (int i = 2; i < instr.operands.length; i++) {
                    ops.critOps.add(instr.operands[i]);
                }
                break;
            case ARRAY_STORE:
                ops.opCode = CriticalOperands.OpCode.NON_ARRAY_LOAD;
                ops.critOps.add(instr.operands[0]);
                break;
            case ARRAY_LOAD:
                ops.opCode = CriticalOperands.OpCode.ARRAY_LOAD;
                ops.critOps.add(instr.operands[1]);
                ops.critOps.add(instr.operands[2]);
                break;
            case LABEL:
            case GOTO:
                break;
        }

        return ops;
    }

    private Set<IRInstruction> markArrayLoad(CriticalOperands critOps, Set<IRInstruction> reachingDefs) {
        Set<IRInstruction> worklistAdditions = new HashSet<>();

        String arrayName = critOps.critOps.get(0).toString();
        String index = critOps.critOps.get(1).toString();

        for (IRInstruction rd : reachingDefs) {
            // we're only concerned with defs writing to index 'index' of array 'arrayName'
            if (rd.opCode != IRInstruction.OpCode.ARRAY_STORE) {
                continue;
            }

            String defArrayName = rd.operands[1].toString();
            String defIndex = rd.operands[2].toString();

            if ((arrayName.equals(defArrayName)) && (index.equals(defIndex))) {
                this.marked.add(rd);
                worklistAdditions.add(rd);
            }
        }

        return worklistAdditions;
    }

    private Set<IRInstruction> markNonArrayLoad(CriticalOperands critOps, Set<IRInstruction> reachingDefs) {
        Set<IRInstruction> worklistAdditions = new HashSet<>();
        
        for (IROperand op : critOps.critOps) {
            String opString = op.toString();
            for (IRInstruction rd : reachingDefs) {
                if (rd.opCode == IRInstruction.OpCode.ARRAY_STORE) { // should be handled in markArrayLoad
                    continue;
                }
                String defOpString = rd.operands[0].toString();
                if (opString.equals(defOpString)) {
                    this.marked.add(rd);
                    worklistAdditions.add(rd);
                }
            }
        }

        return worklistAdditions;
    }

    private Set<IRInstruction> markReachingDefs(CriticalOperands critOps, Set<IRInstruction> reachingDefs) {
        Set<IRInstruction> worklistAdditions = new HashSet<>();
        
        if (critOps.opCode.equals(CriticalOperands.OpCode.ARRAY_LOAD)) {
            worklistAdditions = markArrayLoad(critOps, reachingDefs);
        } else {
            worklistAdditions = markNonArrayLoad(critOps, reachingDefs);
        }

        return worklistAdditions;
    }

    private void mark(CFG cfg) {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> bbMap : basicBlocks.entrySet()) {
            BasicBlock bb = bbMap.getValue();

            for (IRInstruction instr : bb.getInstructions()) {
                if (isCritical(instr)) {
                    assert !this.marked.contains(instr);

                    this.marked.add(instr);
                    this.worklist.add(instr);
                }
            }
        }

        while (!worklist.isEmpty()) {
            Set<IRInstruction> worklistAdditions = new HashSet<>();

            // avoid adding to set while iterating over it (illegal)
            Iterator<IRInstruction> it = this.worklist.iterator();
            while (it.hasNext()) {
                IRInstruction critInstr = it.next();
                it.remove();

                // the operands being read from in a critical operation
                CriticalOperands critOps = criticalOperands(critInstr);

                BasicBlock bb = cfg.getBasicBlock(critInstr);

                // find definitions from other basic blocks that reach critInstr
                Set<IRInstruction> reachingDefs = bb.getReachingDefinitions();
                worklistAdditions.addAll(markReachingDefs(critOps, reachingDefs));

                // find definitions within current block that reach critInstr
                List<IRInstruction> instrs = bb.getInstructions();
                int instrIndex = instrs.indexOf(critInstr);
                assert instrIndex >= 0;
                List<IRInstruction> preceedingInstrsList = instrs.subList(0, instrIndex + 1);
                Set<IRInstruction> preceedingInstrsSet = new HashSet<>(preceedingInstrsList);
                worklistAdditions.addAll(markReachingDefs(critOps, preceedingInstrsSet));
            }
            this.worklist.addAll(worklistAdditions);
        }
    }

    private void sweep(CFG cfg) {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> bbMap : basicBlocks.entrySet()) {
            BasicBlock bb = bbMap.getValue();

            for (IRInstruction instr : bb.getInstructions()) {
                if (!isDefinition(instr)) {  // for now, only worry about removing unnecessary definitions
                    continue;
                }

                if (this.marked.contains(instr)) {
                    continue;
                }

                bb.removeInstruction(instr);
            }
        }
    }

    public void applyMarkSweep(CFG cfg){
        mark(cfg);
        sweep(cfg);
    }
}
