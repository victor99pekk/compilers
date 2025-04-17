import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CFG {

    private Map<Integer, BasicBlock> basicBlocks;
    private BasicBlock startBlock;
    private Set<BasicBlock> leafBlocks;
    private int blockCounter;
    private Map<BasicBlock, Set<BasicBlock>> outgoingEdges;
    private Map<BasicBlock, Set<BasicBlock>> incomingEdges;

    // Private constructor to enforce the use of the Builder
    private CFG() {
        this.basicBlocks = new HashMap<>();
        this.leafBlocks = new HashSet<>();
        this.blockCounter = 0;
        this.outgoingEdges = new HashMap<>();
        this.incomingEdges = new HashMap<>();
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