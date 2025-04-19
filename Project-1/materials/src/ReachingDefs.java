/*
 * Step 1: create map from block number to dataflow analysis sets
 *     - input: block number
 *     - output: class of sets GEN, KILL, PREV_IN, PREV_OUT, CURR_IN, CURR_OUT
 *         - PREV_ and CURR_ sets determine if algorithm has reached a fixed point
 * 
 * Step 2: for each basic block calculate it's GEN set and initialize PREV_OUT to GEN
 * 
 * Step 3: for each basic block, iterate through all other basic blocks and calculate its KILL set
 *     - should just be all definitions that 'redefine' definitions in GEN (?)
 * 
 * Step 4: Run iterative reaching definitions algorithm
 * 
 */

import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import ir.IRInstruction;

 public class ReachingDefs {

    private class DataFlowSets {
        public Set<IRInstruction> GEN;
        public Set<IRInstruction> KILL;
        public Set<IRInstruction> PREV_IN;
        public Set<IRInstruction> PREV_OUT;
        public Set<IRInstruction> CURR_IN;
        public Set<IRInstruction> CURR_OUT;

        public DataFlowSets() {
            this.GEN = new HashSet<IRInstruction>();
            this.KILL = new HashSet<IRInstruction>();
            this.PREV_IN = new HashSet<IRInstruction>();
            this.PREV_OUT = new HashSet<IRInstruction>();
            this.CURR_IN = new HashSet<IRInstruction>();
            this.CURR_OUT = new HashSet<IRInstruction>();
        }
    }

    private CFG cfg;
    private Map<BasicBlock, DataFlowSets> dfsMap;

    public ReachingDefs(CFG cfg) {
        this.cfg = cfg;
        this.dfsMap = new HashMap<>();
    }

    private void initDfsMap() {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> entry : basicBlocks.entrySet()) {
            BasicBlock bb = entry.getValue();
            dfsMap.put(bb, new DataFlowSets());
        }
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
                return true;
            default:
                return false;
        }
    }

    // returns true if definitions write to the same variable
    private boolean defsEqual(IRInstruction def1, IRInstruction def2) {
        assert isDefinition(def1);
        assert isDefinition(def2);

        if (def1.opCode == IRInstruction.OpCode.LABEL || def2.opCode == IRInstruction.OpCode.LABEL) {
            return false;
        }
        
        if ((def1.opCode == IRInstruction.OpCode.ARRAY_STORE) && (def1.opCode != IRInstruction.OpCode.ARRAY_STORE)
            || (def1.opCode != IRInstruction.OpCode.ARRAY_STORE) && (def1.opCode == IRInstruction.OpCode.ARRAY_STORE))
        {
            return false;
        }

        if ((def1.opCode == IRInstruction.OpCode.ARRAY_STORE) && (def1.opCode == IRInstruction.OpCode.ARRAY_STORE)) {
            // verify that operand 1 is the array name and operand 2 is the index
            String array_name_1 = def1.operands[1].toString();
            String array_name_2 = def2.operands[1].toString();
            String array_idx_1 = def1.operands[2].toString();
            String array_idx_2 = def2.operands[2].toString();
            return (array_name_1.equals(array_name_2) && array_idx_1.equals(array_idx_2));
        }

        // otherwise, assume that operand 0 is the variable being assigned to
        String assign1 = def1.operands[0].toString();
        String assign2 = def2.operands[0].toString();
        return assign1.equals(assign2);
    }

    // intialize PREV_OUT to GEN
    private void findGen() {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> entry : basicBlocks.entrySet()) {
            BasicBlock bb = entry.getValue();
            DataFlowSets dfs = dfsMap.get(bb);

            for (IRInstruction instr : bb.getInstructions()) {
                if (!isDefinition(instr))
                    continue;
                
                dfs.GEN.add(instr);
                dfs.PREV_OUT.add(instr);
            }
        }
    }

    private void findKill() {
        Map<Integer, BasicBlock> basicBlocks = cfg.getBasicBlocks();

        for (Map.Entry<Integer, BasicBlock> outer : basicBlocks.entrySet()) {
            BasicBlock outer_bb = outer.getValue();
            DataFlowSets dfs = dfsMap.get(outer_bb);

            for (Map.Entry<Integer, BasicBlock> inner : basicBlocks.entrySet()) {
                BasicBlock inner_bb = inner.getValue();
                if (outer_bb.getId() == inner_bb.getId())
                    continue;
                
                for (IRInstruction outer_instr : dfs.GEN) {
                    for (IRInstruction inner_instr : inner_bb.getInstructions()) {
                        if (!isDefinition(inner_instr))
                            continue;
                        
                        if (!defsEqual(outer_instr, inner_instr))
                            continue;
                        
                        dfs.KILL.add(inner_instr);
                    }
                }   
            }
        }
    }

    private Set<IRInstruction> setDifference(Set<IRInstruction> set1, Set<IRInstruction> set2) {
        Set<IRInstruction> diff = new HashSet<IRInstruction>();

        for (IRInstruction instr1 : set1) {
            boolean contains = false;

            for (IRInstruction instr2 : set2) {
                if (defsEqual(instr1, instr2)) {
                    contains = true;
                    break;
                }
            }

            if (!contains)
                diff.add(instr1);
        }

        return diff;
    }

    private Set<IRInstruction> setUnion(Set<IRInstruction> set1, Set<IRInstruction> set2) {
        Set<IRInstruction> union = new HashSet<IRInstruction>();
        union.addAll(set1);
        union.addAll(set2);
        return union;
    }

    private Set<IRInstruction> unionPredecessorsOuts(BasicBlock bb) {
        Set<BasicBlock> predecessors = cfg.getIncomingEdges(bb);

        Set<IRInstruction> newIn = new HashSet<IRInstruction>();
        for (BasicBlock pred_bb : predecessors) {
            Set<IRInstruction> pred_prev_out = dfsMap.get(pred_bb).PREV_OUT;
            newIn = setUnion(newIn, pred_prev_out);
        }

        return newIn;
    }

    private void findInAndOut() {
        while (true) {
            for (Map.Entry<BasicBlock, DataFlowSets> bbSets : dfsMap.entrySet()) {
                BasicBlock bb = bbSets.getKey();
                DataFlowSets dfs = bbSets.getValue();

                dfs.CURR_IN = unionPredecessorsOuts(bb);
                dfs.CURR_OUT = setUnion(dfs.GEN, setDifference(dfs.PREV_IN, dfs.KILL));
            }

            boolean changed = false;
            for (Map.Entry<BasicBlock, DataFlowSets> bbSets : dfsMap.entrySet()) {
                DataFlowSets dfs = bbSets.getValue();
                if ((!dfs.CURR_IN.equals(dfs.PREV_IN)) || (!dfs.CURR_OUT.equals(dfs.PREV_OUT))) {
                    changed = true;
                    break;
                }
            }

            if (!changed)
                break;
        }
    }

    private void updateReachingDefs() {
        for (Map.Entry<BasicBlock, DataFlowSets> bbSets : dfsMap.entrySet()) {
            BasicBlock bb = bbSets.getKey();
            DataFlowSets dfs = bbSets.getValue();

            for (IRInstruction def : dfs.CURR_IN) {
                bb.addReachingDefinition(def);
            }

            for (IRInstruction def : dfs.CURR_OUT) {
                bb.addOutgoingDefinition(def);
            }
        }
    }

    public CFG findReachingDefs() {
        initDfsMap();
        findGen();
        findKill();
        findInAndOut();
        updateReachingDefs();

        return this.cfg;
    }
 }