import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.IRInstruction;

public class BasicBlock {

    private List<IRInstruction> instructions;
    private Set<IRInstruction> reachingDefinitions;
    private Set<IRInstruction> outgoingDefinitions;
    // private Set<BasicBlock> childrenBlocks;
    // private Set<BasicBlock> parentBlocks;
    private int id;

    public BasicBlock(int id, List<IRInstruction> instructions) {
        this.id = id;
        this.instructions = instructions;
        this.reachingDefinitions = new HashSet<>();
        this.outgoingDefinitions = new HashSet<>();
        // this.childrenBlocks = new HashSet<>();
        // this.parentBlocks = new HashSet<>();
    }

    public int getId(){
        return this.id;
    }

    public Set<IRInstruction> getReachingDefinitions() {
        return reachingDefinitions;
    }

    public Set<IRInstruction> getOutgoingDefinitions() {
        return outgoingDefinitions;
    }

    // public Set<BasicBlock> getChildrenBlocks() {
    //     return childrenBlocks;
    // }

    // public Set<BasicBlock> getParentBlocks() {
    //     return parentBlocks;
    // }

    public void addReachingDefinition(IRInstruction definition) {
        this.reachingDefinitions.add(definition);
    }

    public void addOutgoingDefinition(IRInstruction definition) {
        this.outgoingDefinitions.add(definition);
    }

    // private void addChildBlock(BasicBlock block) {
    //     this.childrenBlocks.add(block);
    // }

    // private void addParentBlock(BasicBlock block) {
    //     this.parentBlocks.add(block);
    // }

    // public static void addEdge(BasicBlock parent, BasicBlock child) {
    //     parent.addChildBlock(child);
    //     child.addParentBlock(parent);
    // }

    public List<IRInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<IRInstruction> instructions) {
        this.instructions = instructions;
    }

    public void addInstruction(IRInstruction instruction) {
        if (this.instructions != null) { 
            this.instructions.add(instruction);
        }
    }

    public void removeInstruction(IRInstruction instruction) {
        if (this.instructions != null) {
            this.instructions.remove(instruction);
        }
    }

    public int getInstructionCount() {
        return this.instructions != null ? this.instructions.size() : 0;
    }

    public IRInstruction getInstruction(int index) {
        if (this.instructions != null && index >= 0 && index < this.instructions.size()) {
            return this.instructions.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid instruction index");
    }
}