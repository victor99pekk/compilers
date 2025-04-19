import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;
import ir.datatype.IRType;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

public class CFG {

    private Map<Integer, BasicBlock> basicBlocks;
    private BasicBlock startBlock;
    private Set<BasicBlock> leafBlocks;
    private int blockCounter;
    private Map<BasicBlock, Set<BasicBlock>> outgoingEdges;
    private Map<BasicBlock, Set<BasicBlock>> incomingEdges;
    private IRFunction function; // the function from which the CFG is created

    // Private constructor to enforce the use of the Builder
    private CFG() {
        this.basicBlocks = new HashMap<>();
        this.leafBlocks = new HashSet<>();
        this.blockCounter = 0;
        this.outgoingEdges = new HashMap<>();
        this.incomingEdges = new HashMap<>();
    }

    private static boolean isBranch(IRInstruction instr) {
        switch(instr.opCode) {
            case BREQ:
            case BRNEQ:
            case BRLT:
            case BRGT:
            case BRLEQ:
            case BRGEQ:
                return true;
            default:
                return false;
        }
    }

    private static boolean isLabel(IRInstruction instr) {
        return instr.opCode == IRInstruction.OpCode.LABEL;
    }

    private static boolean isGoto(IRInstruction instr) {
        return instr.opCode == IRInstruction.OpCode.GOTO;
    }

    private static boolean isHeader(int i, List<IRInstruction> instrs){
        return (i == 0) || isLabel(instrs.get(i)) || (isBranch(instrs.get(i - 1)));
    }

    public static CFG mkCFG(IRFunction func) {
        // basically a copy of the pseudocode from lecture 2, page 21

        Builder cfgBuilder = new CFG.Builder();
        Set<IRInstruction> headers = new HashSet<>();
        Map<Integer, BasicBlock> lineToBlock = new HashMap<>(); // irLineNumber to basic block
        Map<String, BasicBlock> labelToBlock = new HashMap<>();

        cfgBuilder.addFunction(func);
        List<IRInstruction> instrs = func.instructions;

        // FIND HEADERS; INIT BASIC BLOCKS
        for (int i = 0; i < instrs.size(); i++) {
            if (!isHeader(i, instrs)) {
                continue;
            }

            IRInstruction inst = instrs.get(i);
            headers.add(inst);

            BasicBlock bb = new BasicBlock(inst.irLineNumber, new ArrayList<IRInstruction>());
            bb.addInstruction(inst);
            cfgBuilder.addBasicBlock(bb);
            lineToBlock.put(inst.irLineNumber, bb);

            if (isLabel(inst)) {
                String label = inst.operands[0].toString();
                labelToBlock.put(label, bb);
            }
        }

        // POPULATE BASIC BLOCKS; CONNECT BASIC BLOCKS
        int numInstrs = instrs.size();
        for (IRInstruction headerInst : headers) {
            int instrno = instrs.indexOf(headerInst);
            assert instrno >= 0;

            BasicBlock bb = lineToBlock.get(headerInst.irLineNumber);

            // add instructions to bb; stop at the last instruction in bb
            instrno++;
            while (instrno < numInstrs && !isHeader(instrno, instrs)) {
                IRInstruction i = instrs.get(instrno++);
                bb.addInstruction(i);
            }
            instrno--;

            // connect bb to other blocks
            IRInstruction lstInstr = instrs.get(instrno);
            BasicBlock nextBb;
            if (isBranch(lstInstr)) {
                if (instrno + 1 < numInstrs) {
                    nextBb = lineToBlock.get(lstInstr.irLineNumber + 1);
                    cfgBuilder.addEdge(bb, nextBb);
                }
                String target = lstInstr.operands[1].toString();
                nextBb = labelToBlock.get(target);
                cfgBuilder.addEdge(bb, nextBb);
            } else if (isGoto(lstInstr)) {
                String target = lstInstr.operands[1].toString();
                nextBb = labelToBlock.get(target);
                cfgBuilder.addEdge(bb, nextBb);
            } else if (instrno + 1 < numInstrs) {
                nextBb = lineToBlock.get(lstInstr.irLineNumber + 1);
                cfgBuilder.addEdge(bb, nextBb);
            } 
        }

        return cfgBuilder.build();
    }

    public static IRFunction mkFunction(CFG cfg) {
        IRFunction function = cfg.getFunction();
        function.instructions.clear(); // rewrite the optimized function
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> outer : basicBlocks.entrySet()) {
            BasicBlock bb = outer.getValue();
            for (IRInstruction inst : bb.getInstructions()) {
                function.instructions.add(inst);
            }
        }

        return function;
    }

    public void addFunction(IRFunction function) {
        this.function = function;
    }

    public IRFunction getFunction() {
        return this.function;
    }

    public BasicBlock getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(BasicBlock startBlock) {
        this.startBlock = startBlock;
    }

    public void addBasicBlock(BasicBlock block) {
        basicBlocks.put(blockCounter++, block);
        outgoingEdges.put(block, new HashSet<>());
        incomingEdges.put(block, new HashSet<>());
    }

    public void addLeafBlock(BasicBlock block) {
        leafBlocks.add(block);
    }

    public Map<Integer, BasicBlock> getBasicBlocks() {
        return basicBlocks;
    }

    public Set<BasicBlock> getLeafBlocks() {
        return leafBlocks;
    }

    public void addEdge(BasicBlock from, BasicBlock to) {
        outgoingEdges.computeIfAbsent(from, k -> new HashSet<>()).add(to);
        incomingEdges.computeIfAbsent(to, k -> new HashSet<>()).add(from);
    }

    public Set<BasicBlock> getOutgoingEdges(BasicBlock block) {
        return outgoingEdges.getOrDefault(block, new HashSet<>());
    }

    public Set<BasicBlock> getIncomingEdges(BasicBlock block) {
        return incomingEdges.getOrDefault(block, new HashSet<>());
    }

    public static class Builder {
        private CFG cfg;

        public Builder() {
            this.cfg = new CFG();
        }

        public Builder setStartBlock(BasicBlock startBlock) {
            this.cfg.setStartBlock(startBlock);
            return this;
        }

        public Builder addBasicBlock(BasicBlock block) {
            this.cfg.addBasicBlock(block);
            return this;
        }

        public Builder addLeafBlock(BasicBlock block) {
            this.cfg.addLeafBlock(block);
            return this;
        }

        public Builder addEdge(BasicBlock from, BasicBlock to) {
            this.cfg.addEdge(from, to);
            return this;
        }

        public Builder addFunction(IRFunction function) {
            this.cfg.addFunction(function);
            return this;
        }

        public CFG build() {
            return this.cfg;
        }
    }
}