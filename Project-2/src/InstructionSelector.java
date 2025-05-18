import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import ir.IRException;
import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;
import ir.IRReader;
import ir.datatype.IRArrayType;
import ir.datatype.IRType;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;
import main.java.mips.MIPSInstruction;

public class InstructionSelector {

    /* 'temp' virtual registers for storing immediates
     *     Example 1:
     *     Tiger IR branch instructions allow immediates in conditionals (bgt x 2 label)
     *     MIPS branch instructions only take two registers (bgt x y label)
     *     => need to store immediate in a register
     * 
     *     Example 2:
     *     May need to load/store a memory address from/to index into an array
     *     Can use two registers to calculate the address to read/write from/to
     */
    // private static String _tempVirt0 = "temp0";
    // private static String _tempVirt1 = "temp1";
    private static String _tempVirt0 = "$t0";
    private static String _tempVirt1 = "$t1";
    private int pc = 1000;
    private int fp = 1000;
    // private Map<String, List<String>> S_registers_used_by_func = new HashMap<>();
    // private Map<String, List<String>> T_registers_used_by_func = new HashMap<>();
    Stack<Set<String>> S_registers_used_by_func = new Stack<>();
    Stack<Set<String>> T_registers_used_by_func = new Stack<>();
    private int registerT_count = 2;
    private int registerS_count = 0;
    private Map<String, String> S_registers = new HashMap<>();
    private Map<String, String> T_registers = new HashMap<>();
    private String current_func = "";



    private static final Map<String, String> TEMPLATES = new HashMap<>();
    static {
        TEMPLATES.put("ADD",        "add ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("SUB",        "sub ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("MULT",       "mul ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("DIV",        "div  ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("AND",        "and ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("OR",         "or  ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("GOTO",       "j    ${label}");
        TEMPLATES.put("BREQ",       "beq  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRNEQ",      "bne  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLT",       "blt  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGT",       "bgt  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLEQ",      "ble  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGEQ",      "bge  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("ARRAY_LOAD", "la   ${dst}, ${offset}(${base})");
        TEMPLATES.put("ARRAY_STORE","sw   ${src}, ${offset}(${base})");
        TEMPLATES.put("CALL",       "jal ${func}");
        TEMPLATES.put("CALLR",      "jal  ${func}\nmove ${dst}, $v0");
        TEMPLATES.put("RETURN",     "jr   $ra");
        TEMPLATES.put("LABEL",      "${label}:");
        TEMPLATES.put("ASSIGN",     "li ${dst}, ${src}");
    }

    public static boolean isNumeric(String str) {
        if (str == null) return false;
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e1) {
            try {
                Double.parseDouble(str);
                return true;
            } catch (NumberFormatException e2) {
                return false;
            }
        }
    }

    private static void storeNumeric(List<List<String>>list, String dst, String src){
        String tpl = "addi ${dst}, ${src}, $0";
        String lhs   = "";
        String rhs   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String offset= "";
        List<String> lines = new ArrayList<>();
        for (String line : tpl.split("\\n")) {
            String filled = line
                .replace("${dst}",   formatReg(dst))
                .replace("${lhs}",   formatReg(lhs))
                .replace("${rhs}",   formatReg(rhs))
                .replace("${label}", label)
                .replace("${func}",  func)
                .replace("${base}",  formatReg(base))
                .replace("${src}",   formatReg(src))
                .replace("${offset}", offset);
            lines.add("  " + filled);
        }
        list.add(lines);
    }

    private static void sw(List<List<String>>list, String dst, String src){
        String tpl = "sw ${dst}, $0, ${src}";
        String lhs   = "";
        String rhs   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String offset= "";
        List<String> lines = new ArrayList<>();
        for (String line : tpl.split("\\n")) {
            String filled = line
                .replace("${dst}",   formatReg(dst))
                .replace("${lhs}",   formatReg(lhs))
                .replace("${rhs}",   formatReg(rhs))
                .replace("${label}", label)
                .replace("${func}",  func)
                .replace("${base}",  formatReg(base))
                .replace("${src}",   formatReg(src))
                .replace("${offset}", offset);
            lines.add("  " + filled);
        }
        list.add(lines);
    }

    private static void addi(List<List<String>>list, String dst, String rhs, String lhs){
        String tpl = "addi ${dst}, $0, ${src}";
        String src   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String offset= "";
        List<String> lines = new ArrayList<>();
        for (String line : tpl.split("\\n")) {
            String filled = line
                .replace("${dst}",   formatReg(dst))
                .replace("${lhs}",   formatReg(lhs))
                .replace("${rhs}",   formatReg(rhs))
                .replace("${label}", label)
                .replace("${func}",  func)
                .replace("${base}",  formatReg(base))
                .replace("${src}",   formatReg(src))
                .replace("${offset}", offset);
            lines.add("  " + filled);
        }
        list.add(lines);
    }


    private List<List<String>> selectInstruction(IRInstruction instr) {
        List<List<String>>list = new ArrayList<>();
        String op = instr.opCode.name();
        String tpl = TEMPLATES.get(op);
        boolean is_label = false;

        String dst   = "";
        String lhs   = "";
        String rhs   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String src   = "";
        String offset= "";

        if (tpl == null) throw new IllegalArgumentException("Unmapped opcode: " + op);

        if (instr.opCode == IRInstruction.OpCode.LABEL) {
            createLines(list, tpl, dst, lhs, rhs, instr.operands[0].toString(), func, base, src, offset);
            return List.of(List.of(new StringBuilder(instr.operands[0].toString()).append("_").append(this.current_func).append(":").toString()));
        }
        if (instr.opCode == IRInstruction.OpCode.CALLR){
            func = instr.operands[1].toString();
            dst  = instr.operands[0].toString();  
            dst = getRegister(dst, false);
            if (func.equals("geti")) {
                createLines(list, "li $v0, 5", "li", "", "", "", "", "", "", "");
                createLines(list, "syscall", "", "", "", "", "", "", "", "");
                createLines(list, "move ${dst}, $v0", dst, "", "", "", "", "", "", "");
                return list;
            }
        }

        switch (instr.opCode) {
            case MULT:
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();
                if (isNumeric(lhs)){
                    storeNumeric(list, _tempVirt0, lhs);
                    lhs = _tempVirt0;
                }else{
                    lhs = getRegister(lhs, false);
                }
                if (isNumeric(rhs)){
                    storeNumeric(list, _tempVirt1, lhs);
                    rhs = _tempVirt1;
                }else{
                    rhs = getRegister(rhs, false);
                }
                label = getRegister(label, false);
                break;
            case ADD: case DIV:
            case AND: case OR: // todo: finish register allocation
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();
                if (isNumeric(lhs)){
                    storeNumeric(list, _tempVirt0, lhs);
                    lhs = _tempVirt0;
                }else{
                    lhs = getRegister(lhs, false);
                }
                if (isNumeric(rhs)){
                    storeNumeric(list, _tempVirt1, lhs);
                    rhs = _tempVirt1;
                }else{
                    rhs = getRegister(rhs, false);
                }
                label = getRegister(label, false);
                break;
            case SUB:
                dst = instr.operands[0].toString();
                lhs = instr.operands[1].toString();
                rhs = instr.operands[2].toString();
                lhs = getRegister(lhs, false);
                rhs = getRegister(rhs, false);
                dst = getRegister(dst, false);
                if (isNumeric(lhs) || isNumeric(rhs)){
                    // tpl = "addi ${dst}, ${lhs}, -{rhs}";
                    if (!isNumeric(lhs) && isNumeric(rhs)){
                        createLines(list, "addi ${dst}, ${lhs}, -{rhs}", dst, lhs, rhs, label, func, base, src, offset);;
                    }
                    if (isNumeric(lhs) && !isNumeric(rhs)){
                        createLines(list, "addi ${dst}, ${lhs}, -{rhs}", dst, rhs, lhs, label, func, base, src, offset);
                    }else if (!isNumeric(lhs) && !isNumeric(rhs)){
                        storeNumeric(list, _tempVirt0, rhs);
                        rhs = _tempVirt0;
                        storeNumeric(list, _tempVirt1, lhs);
                        lhs = _tempVirt1;
                    }
                }
                break;
            case ASSIGN:
                dst = instr.operands[0].toString();

                src = instr.operands[1].toString();
                if (!isNumeric(src)){
                    src = getRegister(src, false);
                }
                System.out.println(dst);
                dst = getRegister(dst, false);
                // System.out.println(dst);
                // createLines(list, tpl, dst, lhs, rhs, label, func, base, src, offset);
                break;
            case GOTO:
                label = instr.operands[0].toString();
                break;

            case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ: case BREQ:
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();
                if (isNumeric(lhs)){
                    storeNumeric(list, _tempVirt0, lhs);
                    lhs = _tempVirt0;
                }
                if (isNumeric(rhs)){
                    storeNumeric(list, _tempVirt1, lhs);
                    rhs = _tempVirt1;
                }
                lhs = getRegister(lhs, false);
                rhs = getRegister(rhs, false);
                break;
            case ARRAY_LOAD:
                dst    = instr.operands[0].toString();
                base   = instr.operands[1].toString();
                offset = instr.operands[2].toString();

                // If offset is numeric, store it in a temp register
                if (isNumeric(offset)) {
                    storeNumeric(list, _tempVirt0, offset);
                    offset = _tempVirt0;
                } else {
                    offset = getRegister(offset, false);
                }
                base = getRegister(base, false);
                dst  = getRegister(dst, false);

                // sll $t1, offset, 2      # offset (index) * 4 (word size)
                createLines(list, "sll ${dst}, ${lhs}, 2", _tempVirt1, offset, "", "", "", "", "", "");
                // add $t2, base, $t1      # address = base + offset*4
                createLines(list, "add ${dst}, ${lhs}, ${rhs}", _tempVirt0, base, _tempVirt1, "", "", "", "", "");
                // lw dst, 0($t2)          # load word from address
                createLines(list, "lw ${dst}, 0(${base})", dst, "", "", "", "", _tempVirt0, "", "");
                return list;
                // break;
            case ARRAY_STORE:
                src    = instr.operands[0].toString();
                base   = instr.operands[1].toString();
                offset = instr.operands[2].toString();

                if (isNumeric(offset)) {
                    storeNumeric(list, _tempVirt0, offset);
                    offset = _tempVirt0;
                } else {
                    offset = getRegister(offset,    false);
                }
                base = getRegister(base, false);
                src  = getRegister(src, false);

                // sll $t1, offset, 2      # offset (index) * 4 (word size)
                createLines(list, "sll ${dst}, ${lhs}, 2", _tempVirt1, offset, "", "", "", "", "", "");
                // add $t2, base, $t1      # address = base + offset*4
                createLines(list, "add ${dst}, ${lhs}, ${rhs}", _tempVirt0, base, _tempVirt1, "", "", "", "", "");
                // sw src, 0($t2)          # store word to address
                // offset = offset;
                createLines(list, "sw ${src}, {offset}(${base})", src, "", "", "", "", _tempVirt0, "", offset);
                return list;
            case CALL:
                Set<String> used = T_registers_used_by_func.peek();
                prepareFunctionCall(used);
                createLines(list, tpl, dst, lhs, rhs, label, func, base, src, offset);
                restoreFunctionCall(used);
                return list;
            case CALLR:
                func = instr.operands[1].toString();
                dst  = instr.operands[0].toString();
                break;
            case RETURN:
                createLines(list, "j $ra", dst, lhs, rhs, label, func, base, src, offset);
                break;
            case LABEL:
                label = instr.operands[0].toString();
                is_label = true;
                break;
            default:
                throw new IllegalArgumentException("Unsupported opcode: " + op);
        }

        List<String> lines = new ArrayList<>();
        for (String line : tpl.split("\\n")) {
            String filled = line
                .replace("${dst}",   formatReg(dst))
                .replace("${lhs}",   formatReg(lhs))
                .replace("${rhs}",   formatReg(rhs))
                .replace("${label}", label)
                .replace("${func}",  func)
                .replace("${base}",  formatReg(base))
                .replace("${src}",   formatReg(src))
                .replace("${offset}", offset);
            if (is_label) {
                filled = new StringBuilder(filled).append(":").toString();
            }else{
                lines.add("  " + filled);
            }
        }
        list.add(lines);
        return list;
    }

    private String getRegister(String dst, boolean isS) {
        if (isNumeric(dst)) return dst; // immediates stay numeric
        if (!isS){
            if (T_registers.get(dst) == null) {
                T_registers.put(dst, ("$t" + registerT_count));
                registerT_count++;
            }
            dst = T_registers.get(dst);
        }else{
            if (dst.startsWith("$s")) {
                if (S_registers.get(dst) == null) {
                    S_registers.put(dst, "$s" + registerS_count);
                    registerS_count++;
                }
                dst = S_registers.get(dst);
            }
        }
        return dst;
    }

    private static void createLines(List<List<String>>list, String tpl, String dst, String lhs, String rhs,
                                    String label, String func, String base, String src, String offset){
        List<String> lines = new ArrayList<>();
        for (String line : tpl.split("\\n")) {
            String filled = line
                .replace("${dst}",   formatReg(dst))
                .replace("${lhs}",   formatReg(lhs))
                .replace("${rhs}",   formatReg(rhs))
                .replace("${label}", label)
                .replace("${func}",  func)
                .replace("${base}",  formatReg(base))
                .replace("${src}",   formatReg(src))
                .replace("${offset}", offset);
            lines.add("  " + filled);
        }
        list.add(lines);
    }


    private static List<String> loadArguments(IRFunction func, Map<String, Integer> vRegToOffset) {
        List<IRVariableOperand> params = func.parameters;

        int num_mips_arg_registers = 4;
        
        List<String> get_args = new ArrayList<>();
        int i = 0;

        // First four arguments are in the $a_ registers
        for (; i < num_mips_arg_registers; i++) {
            String arg_reg = "$a" + Integer.toString(i);
            String param = params.get(i).getName();
            int param_offset = vRegToOffset.get(param);
            String move_arg = "sw " + arg_reg + ", " + Integer.toString(param_offset) + "(" + arg_reg + ")";
            get_args.add(move_arg);
        }

        // The rest of the arguments are on the stack and need to be loaded in the function prologue
        // TODO: HANDLE MORE THAN 4 ARGUMENTS

        return get_args;
    }

    private static String formatReg(String name) {
        if (name == null || name.isEmpty()) return "";
        if (name.matches("\\d+")) return name;  // immediates stay numeric
        if (name.startsWith("$")) return name;
        return "$" + name;
    }
    
    /* Returns map from "virtual register" name to offset (below) the frame pointer
     *     "Virtual registers" refer to memory locations on the current function's stack frame
    */
    private static Map<String, Integer> virtualRegisterToOffset(IRFunction func) {
        Map<String, Integer> map = new HashMap<>();

        int $ra_and_$sp_space = 2 * MIPSInstruction.WORD_SIZE; // always stored at top of stack
        int offset = -1 * $ra_and_$sp_space;
        
        // give params designated spots on the stack
        List<IRVariableOperand> params = func.parameters;
        for (int i = 0; i < func.parameters.size(); i++, offset -= MIPSInstruction.WORD_SIZE) {
            String p = params.get(i).getName();
            map.put(p, offset);
        }

        // give variables (int-list/float-list) designated spots on the stack
        List<IRVariableOperand> vars = func.variables;
        for (int i = 0; i < func.variables.size(); i++, offset -= MIPSInstruction.WORD_SIZE) {
            String v = vars.get(i).getName();
            map.put(v, offset);
        }

        // allocate space for "temp" virt registers (for storing immediate values in branch instructions)
        map.put(_tempVirt0, offset);
        offset -= MIPSInstruction.WORD_SIZE;
        map.put(_tempVirt1, offset);

        return map;
    }

    private static int calculateStackAllocation(IRFunction fn) {
        int size = 0;
        // space for saved $ra and $fp
        size += 2 * MIPSInstruction.WORD_SIZE;
        // space for all variables (now includes parameters)
        List<IRVariableOperand> allVars = new ArrayList<>();
        allVars.addAll(fn.parameters);
        allVars.addAll(fn.variables);
        for (IRVariableOperand var : allVars) {
            IRType type = var.type;
            if (type instanceof IRArrayType) {
                int elems = ((IRArrayType) type).getSize();
                size += elems * MIPSInstruction.WORD_SIZE;
            } else {
                size += MIPSInstruction.WORD_SIZE;
            }
        }
        // space for temporary variables (_tempVirt[0/1])
        size += 2 * MIPSInstruction.WORD_SIZE;

        // align to 8-byte boundary
        int align = 2 * MIPSInstruction.WORD_SIZE;
        int rem = size % align;
        if (rem != 0) size += align - rem;
        return size;
    }

    // private void prepareFunctionCall(IRFunction fn) {
    //     List<String> used = T_registers_used_by_func.get(fn.name);
    //     if (used == null) {
    //         used = new ArrayList<>();
    //         S_registers_used_by_func.put(fn.name, used);
    //     }
    //     for (int i = 0; i < used.size(); i++) { // Save all used registers
    //         String reg = used.get(i);
    //         if (reg.startsWith("$t")) {
    //             String move = "sw " + reg + ", " + Integer.toString(i * MIPSInstruction.WORD_SIZE) + "$(sp)";
    //             used.add(move);
    //         }
    //     }
    // }

    private void prepareFunctionCall(Set<String> u) {
        List<String> used = new ArrayList<>(u);
        int stack_offset = used.size() * MIPSInstruction.WORD_SIZE;
        for (int i = 0; i < used.size(); i++) { // Save all used registers
            String reg = used.get(i);
            if (reg.startsWith("$t")) {
                String move = "sw " + reg + ", " + Integer.toString(i * MIPSInstruction.WORD_SIZE) + "$(sp)";
                used.add(move);
            }
        }
    }

    private void restoreFunctionCall(Set<String> u) {
        List<String> used = new ArrayList<>(u);
        int stack_offset = used.size() * MIPSInstruction.WORD_SIZE;
        for (int i = 0; i < used.size(); i++) { // Restore all used registers
            String reg = used.get(i);
            if (reg.startsWith("$t")) {
                String move = "lw " + reg + ", " + Integer.toString(i * MIPSInstruction.WORD_SIZE) + "$(sp)";
                used.add(move);
            }
        }
    }

        


    public List<String> instructionSelection(IRProgram program) {
        List<String> mips = new ArrayList<>();
        mips.add(".text");
        Collections.reverse(program.functions); // to print the main function first
        for (IRFunction fn : program.functions) {
            T_registers_used_by_func.push(new HashSet<>());
            S_registers_used_by_func.push(new HashSet<>());
            registerS_count = 0;
            registerT_count = 2;
            current_func = fn.name;
            S_registers.clear();
            T_registers.clear();

            mips.add(fn.name + ":");
            int offset = calculateStackAllocation(fn);
            this.fp = this.pc;
            this.pc += offset;
            mips.add("  move $fp, $sp");
            mips.add("  addi $sp, $sp, -" + offset);
            for (IRInstruction instr : fn.instructions) {
                List<List<String>> list = selectInstruction(instr);
                for (List<String> instruction : list){
                    mips.addAll(instruction);
                }
            }
            mips.add("");
        }
        return mips;
    }

    public static void writeMipsToFile(List<String> lines, String path) throws IOException {
        Files.write(Paths.get(path), lines);
    }

    public static void main(String[] args) throws IOException, IRException {
        IRProgram prog = new IRReader().parseIRFile(args[0]);
        InstructionSelector is = new InstructionSelector();
        List<String> mips = is.instructionSelection(prog);
        // List<String> mips = instructionSelection(prog);
        writeMipsToFile(mips, "out.s");
        System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
    }
}
