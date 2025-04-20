import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.IRInstruction;
import ir.operand.IROperand;

public class MarkSweep {
    
    private Set<IRInstruction> marked;
    private Set<IRInstruction> worklist;

    // array_load instructions need to be handled specially
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

    private void mark(){}
    private void sweep(){}

    public void applyMarkSweep(CFG cfg){
        ///.... todo ....
        
    }
}
