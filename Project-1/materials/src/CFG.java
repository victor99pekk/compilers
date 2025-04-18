import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;
import ir.operand.IROperand;

public class CFG {

    private Map<Integer, BasicBlock> basicBlocks;
    private Map<Integer, BasicBlock> lineToBlock;
    private BasicBlock startBlock;
    private Set<BasicBlock> leafBlocks;
    private int blockCounter;
    private Map<BasicBlock, Set<BasicBlock>> outgoingEdges;
    private Map<BasicBlock, Set<BasicBlock>> incomingEdges;
    private Set<BasicBlock> blocksInCFG;
    private Map<String, Integer> labelToLine;

    // Private constructor to enforce the use of the Builder
    private CFG() {
        this.basicBlocks = new HashMap<>();
        this.leafBlocks = new HashSet<>();
        this.blockCounter = 0;
        this.outgoingEdges = new HashMap<>();
        this.incomingEdges = new HashMap<>();
        this.blocksInCFG = new HashSet<>();
        this.labelToLine = new HashMap<>();
    }

    public static CFG mkCFG_copy(IRFunction irProgram){

        // program contains functions
        // functions contains list of intructions
        CFG cfg = new CFG.Builder().build();
        Set<IRInstruction> headers = new HashSet<>();
        for (IRFunction function : irProgram.functions){            // iterate functions
            for (int i = 0; i < function.instructions.size(); i++){ // Find headers
             // header if...
                if (CFG.isHeader(i, function.instructions.get(i))) { // i == 0
                    headers.add(function.instructions.get(i));
                    BasicBlock bb = new Ba
                    cfg.lineToBlock.put(function.instructions.get(i).irLineNumber, )
                }
            }
        }

        // CREATE THE BASICBLOCKS
        int basicBlockCounter = 0;
        Set<BasicBlock> blocksInCFG = new HashSet<>();
        for (IRFunction function : irProgram.functions){            // iterate functions
            int instructionIndex = 0;
            int functionLength = function.instructions.size();
            while (instructionIndex < functionLength){
                IRInstruction instruction = function.instructions.get(instructionIndex);
                List<IRInstruction> instructionsInBlock = new ArrayList<>();
                while (instructionIndex + 1 < functionLength && !CFG.isHeader(instructionIndex, instruction)){
                    instructionsInBlock.add(instruction);
                }
                if (instructionIndex == functionLength) instructionsInBlock.add(instruction);
                int LineNbrFirstInstruction = instructionsInBlock.get(0).irLineNumber;
                BasicBlock basicBlock = new BasicBlock(LineNbrFirstInstruction, instructionsInBlock);
                blocksInCFG.add(basicBlock);
            }
        }

        // CONNECT THE BASICBLOCK - NODES
        // private Map<Integer, BasicBlock> basicBlocks;
        for (BasicBlock basicBlock : blocksInCFG){
            // find which block the lastline points to?
            // we have a map<header - lineNBR, BasicBlock> that can be useful
            // how do we kow what comes after the last line?
            
            // if last line is a goto, then the next line is the one it points to.
            // else, it is
        }

        return null;
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

    private static boolean isHeader(int i, IRInstruction instruction){
        return (i == 0) || (instruction.opCode == IRInstruction.OpCode.LABEL) || (isBranch(instruction));
    }

    public static CFG mkCFG(IRFunction function){

        // program contains functions
        // functions contains list of intructions
        CFG cfg = new CFG.Builder().build();
        Set<IRInstruction> headers = new HashSet<>();
        for (int i = 0; i < function.instructions.size(); i++){ // Find headers
            // header if...
            if (CFG.isHeader(i, function.instructions.get(i))) { // i == 0
                headers.add(function.instructions.get(i));
                
                // cfg.lineToBlock.put(function.instructions.get(i).irLineNumber, )
            }
        }

        // CREATE THE BASICBLOCKS
        int basicBlockCounter = 0;
        Set<BasicBlock> blocksInCFG = new HashSet<>();
        int instructionIndex = 0;
        int functionLength = function.instructions.size();
        List<IRInstruction> instructionsInBlock = new ArrayList<>();
        while (instructionIndex < functionLength){
            IRInstruction instruction = function.instructions.get(instructionIndex);
            while (instructionIndex + 1 < functionLength && !CFG.isHeader(instructionIndex, instruction)){
                instructionsInBlock.add(instruction);
            }
            if (instructionIndex == functionLength) instructionsInBlock.add(instruction);

            int LineNbrFirstInstruction = instructionsInBlock.get(0).irLineNumber;
            BasicBlock basicBlock = new BasicBlock(LineNbrFirstInstruction, instructionsInBlock);
            blocksInCFG.add(basicBlock);
            instructionsInBlock.clear();
        }

        // CONNECT THE BASICBLOCK - NODES
        // private Map<Integer, BasicBlock> basicBlocks;
        for (BasicBlock basicBlock : blocksInCFG){
            // find which block the lastline points to?
            // we have a map<header - lineNBR, BasicBlock> that can be useful
            // how do we kow what comes after the last line?
            
            // if last line is a goto / branch, then the next edge points to the basicblock, whos address the goto points to
            // else, it is consecutive line

            IRInstruction instruction = basicBlock.getInstructions().getLast();
            IRInstruction.OpCode operand = instruction.opCode;

            if (operand == IRInstruction.OpCode.GOTO) {
                String targetLabel = instruction.operands[0].toString();
            }

        }

        return null;
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

        public CFG build() {
            return this.cfg;
        }
    }
}