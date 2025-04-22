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
    private List<IROperand> criticalOperands(IRInstruction instr) {
        List<IROperand> ops = new ArrayList<>();
        
        switch (instr.opCode) {
            case ASSIGN:
                if (instr.operands.length == 2) { // variable assign (Tiger IR page 3)
                    ops.add(instr.operands[1]);
                } else { // array assign (Tiger IR page 5)
                    ops.add(instr.operands[2]);
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
                ops.add(instr.operands[1]);
                ops.add(instr.operands[2]);
                break;
            case RETURN:
                ops.add(instr.operands[0]);
                break;
            case CALL:
                for (int i = 1; i < instr.operands.length; i++) {
                    ops.add(instr.operands[i]);
                }
                break;
            case CALLR:
                for (int i = 2; i < instr.operands.length; i++) {
                    IROperand temp = instr.operands[i];
                    ops.add(temp);
                }
                break;
            case ARRAY_STORE:
                ops.add(instr.operands[0]);
                ops.add(instr.operands[2]);
                break;
            case ARRAY_LOAD:
                ops.add(instr.operands[1]);
                ops.add(instr.operands[2]);
                break;
            case LABEL:
            case GOTO:
                break;
        }

        return ops;
    }

    private Set<IRInstruction> markReachingDefs(List<IROperand> critOps, Set<IRInstruction> reachingDefs) {
        Set<IRInstruction> worklistAdditions = new HashSet<>();
        
        for (IROperand op : critOps) {
            String opString = op.toString();
            for (IRInstruction rd : reachingDefs) {
                if (this.marked.contains(rd)) {
                    continue;
                }
                
                String defOpString;
                if (rd.opCode == IRInstruction.OpCode.ARRAY_STORE) { // get array being written to
                    defOpString = rd.operands[1].toString();
                } else { // else get the (int or float) variable being written to
                    defOpString = rd.operands[0].toString();
                }

                if (opString.equals(defOpString)) {
                    this.marked.add(rd);
                    worklistAdditions.add(rd);
                }
            }
        }

        return worklistAdditions;
    }

    private Set<IRInstruction> markInternalReachingDefs(List<IROperand> critOps, List<IRInstruction> reachingDefs) {
        Set<IRInstruction> worklistAdditions = new HashSet<>();
        Set<IROperand> opsToRemove = new HashSet<>();

        for (IROperand op : critOps) {
            String opString = op.toString();
            for (int i = reachingDefs.size() - 1; i >= 0; i--) {
                IRInstruction rd = reachingDefs.get(i);

                if (this.marked.contains(rd)) {
                    continue;
                }
                
                String defOpString;
                if (rd.opCode == IRInstruction.OpCode.ARRAY_STORE) { // get array being written to
                    defOpString = rd.operands[1].toString();
                } else { // else get the (int or float) variable being written to
                    defOpString = rd.operands[0].toString();
                }

                if (opString.equals(defOpString)) {
                    this.marked.add(rd);
                    worklistAdditions.add(rd);
                    opsToRemove.add(op);
                    break;
                }
            }
        }

        for (IROperand op : opsToRemove) {
            int idx = critOps.indexOf(op);
            assert idx >= 0;
            critOps.remove(idx);
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
                List<IROperand> critOps = criticalOperands(critInstr);

                BasicBlock bb = cfg.getBasicBlock(critInstr);

                // find definitions within current block that reach critInstr
                List<IRInstruction> instrs = bb.getInstructions();
                int instrIndex = instrs.indexOf(critInstr);
                assert instrIndex >= 0;
                List<IRInstruction> preceedingInstrsList = instrs.subList(0, instrIndex + 1);
                worklistAdditions.addAll(markInternalReachingDefs(critOps, preceedingInstrsList));

                // find definitions from other basic blocks that reach critInstr
                Set<IRInstruction> reachingDefs = bb.getReachingDefinitions();
                worklistAdditions.addAll(markReachingDefs(critOps, reachingDefs));
            }
            this.worklist.addAll(worklistAdditions);
        }
    }

    private void sweep(CFG cfg) {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> bbMap : basicBlocks.entrySet()) {
            BasicBlock bb = bbMap.getValue();

            List<IRInstruction> instrsToRemove = new ArrayList<>();
            for (IRInstruction instr : bb.getInstructions()) {
                if (!isDefinition(instr)) {  // for now, only worry about removing unnecessary definitions
                    continue;
                }

                if (this.marked.contains(instr)) {
                    continue;
                }

                instrsToRemove.add(instr);
            }

            for (IRInstruction instr : instrsToRemove) {
                bb.removeInstruction(instr);
            }
        }
    }

    public void applyMarkSweep(CFG cfg){
        mark(cfg);
        sweep(cfg);
    }
}
