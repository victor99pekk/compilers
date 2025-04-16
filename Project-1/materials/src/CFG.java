import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class CFG {

    private Map<Integer, BasicBlock> basicBlocks;
    private BasicBlock startBlock;
    private Set<BasicBlock> leafBlocks;
    private int blockCounter;

    public CFG() {
        this.basicBlocks = new HashMap<>();
        this.leafBlocks = new HashSet<>();
        this.blockCounter = 0;
    }

    public BasicBlock getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(BasicBlock startBlock) {
        this.startBlock = startBlock;
    }

    public void addBasicBlock(BasicBlock block) {
        basicBlocks.put(blockCounter++, block);
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
}