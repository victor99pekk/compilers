import java.io.IOException;
import java.lang.foreign.Linker.Option;
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

public class IntraBlockAllocInstructionSelector {
    
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
    private static String _tempVirt0 = "temp0";
    private static String _tempVirt1 = "temp1";

    // default MIPS architectural register to use during naive register allocation
    private static String _default_dest = "$t0";
    private static String _default_lhs = "$t1";
    private static String _default_rhs = "$t2";

    // private static String _tempVirt0 = "$t0";
    // private static String _tempVirt1 = "$t1";
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
    private Map<String, String> func_to_array = new HashMap<>();
    private Map<String, String> array_to_stack = new HashMap<>();

    // registers to be used in register allocation scheme
    private static final List<String> REGISTERS = new ArrayList<String>();
    static {
        REGISTERS.add("$t3");
        REGISTERS.add("$t4");
        REGISTERS.add("$t5");
        REGISTERS.add("$t6");
        REGISTERS.add("$t7");
        REGISTERS.add("$t8");
        REGISTERS.add("$t9");
    }

    // private

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

    // store from architectural register into virtual register
    private void storeVirtualRegister(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off)
    {
        int offset = v_reg_to_off.get(virtReg);
        createLines(list, "sw ${src}, ${offset}(${base})","","","","","","$fp", archReg, Integer.toString(offset));
    }

    // load from virtual register into architectural register
    private void loadVirtualRegister(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off)
    {
        int offset = v_reg_to_off.get(virtReg);
        createLines(list, "lw ${dst}, ${offset}(${base})", archReg,"","","","","$fp", "", Integer.toString(offset));
    }


    // Move from architectural register to virtual register
    // If virtual register has an allocated architectural register, use that register
    private void moveToVirtReg(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off,
        Map<String, String> v_reg_to_arch_reg)
    {
        if (v_reg_to_arch_reg.containsKey(virtReg)) {
            String dst_reg = v_reg_to_arch_reg.get(virtReg);
            createLines(list, "move ${dst}, ${src}", dst_reg, "", "", "", "", "", archReg, "");
        } else {
            storeVirtualRegister(list, archReg, virtReg, v_reg_to_off);
        }
    }

    // Move from virtual register to architectural register
    // If virtual register has an allocated architectural register, use that register
    private void moveToArchReg(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off,
        Map<String, String> v_reg_to_arch_reg)
    {
        if (v_reg_to_arch_reg.containsKey(virtReg)) {
            String src_reg = v_reg_to_arch_reg.get(virtReg);
            createLines(list, "move ${dst}, ${src}", archReg, "", "", "", "", "", src_reg, "");
        } else {
            loadVirtualRegister(list, archReg, virtReg, v_reg_to_off);
        }
    }

    private void li(List<List<String>>list, String dst, String imm) {
        createLines(list, "li ${dst}, ${src}", dst, "", "", "", "", "", imm, "");
    }

    private String arithTigerOpToMipsOp(IRInstruction.OpCode op) {
        // need to change mult to mul
        switch (op) {
            case MULT:
                return "mul";
            default:
                return op.toString();
        }
    }

    private void arithAndLogicInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        String lhs  = instr.operands[1].toString();
        String rhs  = instr.operands[2].toString();
        String dst = instr.operands[0].toString();
        
        // If operand is immediate, load it into an architectural register and use that register
        // Otherwise, the operand is a virtual register and must be loaded into a physical register
        if (isNumeric(rhs)){
            li(list, _default_rhs, rhs);
        } else {
            // loadVirtualRegister(list, _default_rhs, rhs, v_reg_to_off);
            moveToArchReg(list, _default_rhs, rhs, v_reg_to_off, v_reg_to_arch_reg);
        }

        if (isNumeric(lhs)){
            li(list, _default_lhs, lhs);
        } else {
            // loadVirtualRegister(list, _default_lhs, lhs, v_reg_to_off);
            moveToArchReg(list, _default_lhs, lhs, v_reg_to_off, v_reg_to_arch_reg);
        }
        
        // Create mips version of the Tiger IR instruction
        String op = arithTigerOpToMipsOp(instr.opCode);
        createLines(list, "${label} ${dst}, ${lhs}, ${rhs}", _default_dest, _default_lhs, _default_rhs, op,"","","","");

        // Move result into virtual register
        // storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
        moveToVirtReg(list, _default_dest, dst, v_reg_to_off, v_reg_to_arch_reg);
    }

    private void assignInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        String dst = instr.operands[0].toString();
        String src = instr.operands[1].toString();
        
        // If operand is immediate, then it's essentially just a load immediate instruction
        if (isNumeric(src)) {
            li(list, _default_dest, src);
            // storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
            moveToVirtReg(list, _default_dest, dst, v_reg_to_off, v_reg_to_arch_reg);
            return;
        }

        // Otherwise, move between registers
        // loadVirtualRegister(list, _default_lhs, src, v_reg_to_off);
        moveToArchReg(list, _default_dest, src, v_reg_to_off, v_reg_to_arch_reg);
        createLines(list, "move ${dst}, ${src}", _default_dest, "", "", "", "", "", _default_lhs, "");
        // storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
        moveToVirtReg(list, _default_dest, dst, v_reg_to_off, v_reg_to_arch_reg);
    }


    private String tigerBranchOpToMipsBranchOps(IRInstruction.OpCode tigerOp) {
        switch (tigerOp) {
            case BREQ:
                return "beq";
            case BRGT:
                return "bgt";
            case BRGEQ:
                return "bge";
            case BRLEQ:
                return "ble";
            case BRLT:
                return "blt";
            case BRNEQ:
                return "bne";
            default:
                throw new IllegalArgumentException("Expected a branch op. Instead got " + tigerOp.toString());
        }
    }

    private void branchInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr, String func_name) {
        String lhs   = instr.operands[1].toString();
        String rhs   = instr.operands[2].toString();
        
        String label = instr.operands[0].toString();
        String local_label = label + "_" + func_name;
        
        // If operand is immediate, load it into an architectural register and use that register
        // Otherwise, the operand is a virtual register and must be loaded into a physical register
        if (isNumeric(lhs)){
            li(list, _default_lhs, lhs);
        } else {
            // loadVirtualRegister(list, _default_lhs, lhs, v_reg_to_off);
            moveToArchReg(list, _default_lhs, lhs, v_reg_to_off, v_reg_to_arch_reg);
        }
        if (isNumeric(rhs)){
            li(list, _default_rhs, rhs);
        } else {
            // loadVirtualRegister(list, _default_rhs, rhs, v_reg_to_off);
            moveToArchReg(list, _default_rhs, rhs, v_reg_to_off, v_reg_to_arch_reg);
        }

        String op = tigerBranchOpToMipsBranchOps(instr.opCode);
        createLines(list, "${label} ${lhs}, ${rhs}, ${offset}", "", _default_lhs, _default_rhs, op, "", "", "", local_label);
    }

    private void arrayLoadInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        /*
         * Use default lhs register as the offset and default rhs register as the base
         * Use lhs to calculate the address.
         * Store in default destination register
         */
        
        String dst    = instr.operands[0].toString();
        String base   = instr.operands[1].toString();
        String offset = instr.operands[2].toString();

        // _default_lhs = offset
        if (isNumeric(offset)){ // If offset is numeric, store it in a temp register
            li(list, _default_lhs, offset);
        } else {
            // loadVirtualRegister(list, _default_lhs, offset, v_reg_to_off);
            moveToArchReg(list, _default_lhs, offset, v_reg_to_off, v_reg_to_arch_reg);
        }
        // _default_rhs = base
        // loadVirtualRegister(list, _default_rhs, base, v_reg_to_off);
        moveToArchReg(list, _default_rhs, base, v_reg_to_off, v_reg_to_arch_reg);
        // _default_lhs <<= 2
        createLines(list, "sll ${dst}, ${lhs}, 2", _default_lhs, _default_lhs, "", "", "", "", "", "");
        // _default_lhs += base    # address = base + offset*4
        createLines(list, "add ${dst}, ${lhs}, ${rhs}", _default_lhs, _default_lhs, _default_rhs, "", "", "", "", "");

        // lw dst, 0($t)          # load word from address
        createLines(list, "lw ${dst}, 0(${base})", _default_dest, "", "", "", "", _default_lhs, "", "");
        // store into virtual register
        // storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
        moveToVirtReg(list, _default_dest, dst, v_reg_to_off, v_reg_to_arch_reg);
    }

    private void arrayStoreInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        /*
         * Use default lhs register as the offset and default rhs register as the base
         * Use lhs to calculate the address
         */
        
        String src    = instr.operands[0].toString();
        String base   = instr.operands[1].toString();
        String offset = instr.operands[2].toString();

        // _default_lhs = offset
        if (isNumeric(offset)){ // If offset is numeric, store it in a temp register
            li(list, _default_lhs, offset);
        } else {
            // loadVirtualRegister(list, _default_lhs, offset, v_reg_to_off);
            moveToArchReg(list, _default_lhs, offset, v_reg_to_off, v_reg_to_arch_reg);
        }
        // _default_rhs = base
        // loadVirtualRegister(list, _default_rhs, base, v_reg_to_off);
        moveToArchReg(list, _default_rhs, base, v_reg_to_off, v_reg_to_arch_reg);
        // _default_lhs <<= 2
        createLines(list, "sll ${dst}, ${lhs}, 2", _default_lhs, _default_lhs, "", "", "", "", "", "");
        // _default_lhs += base    # address = base + offset*4
        createLines(list, "add ${dst}, ${lhs}, ${rhs}", _default_lhs, _default_lhs, _default_rhs, "", "", "", "", "");
        // _default_dst = src
        if (isNumeric(src)) {
            li(list, _default_dest, src);
        } else {
            // loadVirtualRegister(list, _default_dest, src, v_reg_to_off);
            moveToArchReg(list, _default_dest, src, v_reg_to_off, v_reg_to_arch_reg);
        }

        // sw src, 0($t)          # store word to address
        // offset = offset;
        createLines(list, "sw ${dst}, 0(${base})", _default_dest, "", "", "", "", _default_lhs, "", "");
    }

    private List<String> popRegisters(Map<String, String> v_reg_to_arch_reg, Map<String, Integer> v_reg_to_off) {
        List<String> saves = new ArrayList<>();
        
        for (Map.Entry<String, String> virt_arch_pair : v_reg_to_arch_reg.entrySet()) {
            String v_reg = virt_arch_pair.getKey();
            int offset = v_reg_to_off.get(v_reg);
            
            String a_reg = virt_arch_pair.getValue();

            // lw $<a_reg>, <offset>($fp)
            String s = "  lw " + a_reg + ", " + offset + "($fp)";
            saves.add(s);
        }

        return saves;
    }

    private List<String> pushRegisters(Map<String, String> v_reg_to_arch_reg, Map<String, Integer> v_reg_to_off) {
        List<String> stores = new ArrayList<>();
        
        for (Map.Entry<String, String> virt_arch_pair : v_reg_to_arch_reg.entrySet()) {
            String v_reg = virt_arch_pair.getKey();
            int offset = v_reg_to_off.get(v_reg);
            
            String a_reg = virt_arch_pair.getValue();

            // sw $<a_reg>, <offset>($fp)
            String s = "  sw " + a_reg + ", " + offset + "($fp)";
            stores.add(s);
        }

        return stores;
    }

    private void allocateSpaceOnStack(List<List<String>> list, int size) {
        // allocate space for local variables
        createLines(list, "addi $sp, $sp, -{size}","","","","","","","","");
    }

    private void callInstr(List<List<String>> list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        String func = instr.operands[0].toString();

        // Put values into arg registers
        for (int i = 1; i < instr.operands.length; i++) {
            String operand = instr.operands[i].toString();
            String arg_reg = "$a" + Integer.toString(i - 1);

            if (isNumeric(operand)){ // If offset is numeric, store it in a temp register
                li(list, arg_reg, operand);
            } else {
                // loadVirtualRegister(list, arg_reg, operand, v_reg_to_off);
                moveToArchReg(list, arg_reg, operand, v_reg_to_off, v_reg_to_arch_reg);
            }
        }

        // save temp registers
        List<String> pushes = pushRegisters(v_reg_to_arch_reg, v_reg_to_off);
        list.add(pushes);

        // save $fp and $ra
        createLines(list, "addi $sp, $sp, -8","","","","","","","","");
        createLines(list, "sw $fp, 0($sp)","","","","","","","","");
        createLines(list, "sw $ra, 4($sp)","","","","","","","","");

        // call function
        createLines(list, "jal ${func}", "", "", "", "", func, "", "", "");

        // restore $fp and $ra
        createLines(list, "lw $fp, 0($sp)","","","","","","","","");
        createLines(list, "lw $ra, 4($sp)","","","","","","","","");
        createLines(list, "addi $sp, $sp, 8","","","","","","","","");

        // restore temp registers
        List<String> pops = popRegisters(v_reg_to_arch_reg, v_reg_to_off);
        list.add(pops);
    }

    private void callrInstr(List<List<String>> list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, IRInstruction instr) {
        String func = instr.operands[1].toString();
        String ret = instr.operands[0].toString();

        // Put values into arg registers
        for (int i = 2; i < instr.operands.length; i++) {
            String operand = instr.operands[i].toString();
            String arg_reg = "$a" + Integer.toString(i - 2);

            if (isNumeric(operand)){ // If offset is numeric, store it in a temp register
                li(list, arg_reg, operand);
            } else {
                // loadVirtualRegister(list, arg_reg, operand, v_reg_to_off);
                moveToArchReg(list, arg_reg, operand, v_reg_to_off, v_reg_to_arch_reg);
            }
        }

        // save temp registers
        List<String> pushes = pushRegisters(v_reg_to_arch_reg, v_reg_to_off);
        list.add(pushes);

        // save $fp and $ra
        createLines(list, "addi $sp, $sp, -8","","","","","","","","");
        createLines(list, "sw $fp, 0($sp)","","","","","","","","");
        createLines(list, "sw $ra, 4($sp)","","","","","","","","");

        // call function
        createLines(list, "jal ${func}", "", "", "", "", func, "", "", "");

        // restore $fp and $ra
        createLines(list, "lw $fp, 0($sp)","","","","","","","","");
        createLines(list, "lw $ra, 4($sp)","","","","","","","","");
        createLines(list, "addi $sp, $sp, 8","","","","","","","","");

        // restore temp registers
        List<String> pops = popRegisters(v_reg_to_arch_reg, v_reg_to_off);
        list.add(pops);

        // ret = $v0  # store return value in virtual register
        // storeVirtualRegister(list, "$v0", ret, v_reg_to_off);
        moveToVirtReg(list, "$v0", ret, v_reg_to_off, v_reg_to_arch_reg);
    }

    private void getSyscalls(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, String func, String dst) {
        if (func.equals("geti")) {
            createLines(list, "li $v0, 5", "li", "", "", "", "", "", "", "");
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
            
        }
        else if (func.equals("getf")) {
            createLines(list, "li $v0, 6", "", "", "", "", "", "", "", "");
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
        }
        else if (func.equals("getc")) {
            createLines(list, "li $v0, 12", "", "", "", "", "", "", "", "");
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
        }

        moveToVirtReg(list, "$v0", dst, v_reg_to_off, v_reg_to_arch_reg);
    }

    private void putSyscalls(List<List<String>>list, Map<String, Integer> v_reg_to_off, Map<String, String> v_reg_to_arch_reg, String func, String arg) {
        if (func.equals("puti")) {
            createLines(list, "li $v0, 1", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $a0, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                moveToArchReg(list, "$a0", arg, v_reg_to_off, v_reg_to_arch_reg);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
        }
        else if (func.equals("putf")) {
            createLines(list, "li $v0, 2", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $f12, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                moveToArchReg(list, "$f12", arg, v_reg_to_off, v_reg_to_arch_reg);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
        }
        else if (func.equals("putc")) {
            createLines(list, "li $v0, 11", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $a0, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                moveToArchReg(list, "$a0", arg, v_reg_to_off, v_reg_to_arch_reg);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
        }
    }

    private List<List<String>> selectInstruction(
        IRInstruction instr,
        Map<String, Integer> v_reg_to_off,
        Map<String, String> v_reg_to_arch_reg,
        String func_name,
        int spOffset) 
    {
        
        List<List<String>>list = new ArrayList<>();
        String op = instr.opCode.name();
        String tpl = TEMPLATES.get(op);

        String dst   = "";
        String lhs   = "";
        String rhs   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String src   = "";
        String offset= "";

        if (tpl == null) throw new IllegalArgumentException("Unmapped opcode: " + op);


        // Special instructions
        if (instr.opCode == IRInstruction.OpCode.LABEL) {
            createLines(list, tpl, dst, lhs, rhs, instr.operands[0].toString(), func, base, src, offset);
            return List.of(List.of(new StringBuilder(instr.operands[0].toString()).append("_").append(this.current_func).append(":").toString()));
        }
        if (instr.opCode == IRInstruction.OpCode.CALLR){
            func = instr.operands[1].toString();
            dst  = instr.operands[0].toString();  
            if (func.equals("geti") || func.equals("getf") || func.equals("getc")) {
                getSyscalls(list, v_reg_to_off, v_reg_to_arch_reg, func, dst);
                return list;
            }
        }
        if (instr.opCode == IRInstruction.OpCode.CALL) {
            func = instr.operands[0].toString();
            lhs = instr.operands[1].toString();
            if (func.equals("puti") || func.equals("putf") || func.equals("putc")) {
                putSyscalls(list, v_reg_to_off, v_reg_to_arch_reg, func, lhs);
                return list;
            }
        }

        switch (instr.opCode) {
            case ADD: case SUB: case MULT: case DIV: case AND: case OR:
                arithAndLogicInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case ASSIGN:
                assignInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case GOTO:
                label = instr.operands[0].toString();
                String local_label = label + "_" + func_name;
                createLines(list, "j ${label}", "", "", "", local_label, "", "", "", "");
                return list;
            case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ: case BREQ:
                branchInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr, func_name);
                return list;
            case ARRAY_LOAD:
                arrayLoadInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case ARRAY_STORE:
                arrayStoreInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case CALL:
                callInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case CALLR:
                callrInstr(list, v_reg_to_off, v_reg_to_arch_reg, instr);
                return list;
            case RETURN:
                String retval = instr.operands[0].toString();
                if (isNumeric(retval)){ // If offset is numeric, store it in a temp register
                    li(list, "$v0", retval);
                } else {
                    // loadVirtualRegister(list, "$v0", retval, v_reg_to_off);
                    moveToArchReg(list, "$v0", retval, v_reg_to_off, v_reg_to_arch_reg);
                }
                createLines(list, "addi $sp, $sp, ${offset}","","","","","","","",Integer.toString(spOffset));
                createLines(list, "jr $ra", dst, lhs, rhs, label, func, base, src, offset);
                return list;

            default:
                throw new IllegalArgumentException("Unsupported opcode: " + op);
        }
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


    private List<String> loadArguments(IRFunction func, Map<String, Integer> vRegToOffset) {
        List<IRVariableOperand> params = func.parameters;

        int num_mips_arg_registers = 4;
        
        List<String> get_args = new ArrayList<>();
        int i = 0;

        // First four arguments are in the $a_ registers
        for (; (i < params.size()) && (i < num_mips_arg_registers); i++) {
            String arg_reg = "$a" + Integer.toString(i);
            String param = params.get(i).getName();
            int param_offset = vRegToOffset.get(param);
            String move_arg = "  sw " + arg_reg + ", " + Integer.toString(param_offset) + "($fp)";
            
            get_args.add(move_arg);
        }

        return get_args;
    }


    private static String formatReg(String name) {
        if (name == null || name.isEmpty()) return "";
        if (isNumeric(name)) return name;  // immediates stay numeric
        if (name.startsWith("$")) return name;
        return "$" + name;
    }

    /* Returns map from "virtual register" name to offset (below) the frame pointer
     *     "Virtual registers" refer to memory locations on the current function's stack frame
    */
    private static Map<String, Integer> virtualRegisterToOffset(IRFunction func, List<String> list) {
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
        for (int i = 0; i < func.variables.size(); i++) {
            String v = vars.get(i).getName();

            if (map.containsKey(v))
                continue;
            
            IRType type = vars.get(i).type;
            if (type instanceof IRArrayType) {
                int elems = ((IRArrayType) type).getSize();
                map.put(vars.get(i).getName(), offset);
                list.add("  li $v0, 9");
                list.add("  li $a0, " + elems * MIPSInstruction.WORD_SIZE);
                list.add("  syscall");
                list.add("  sw $v0, " + offset + "($fp)");

                offset -= MIPSInstruction.WORD_SIZE;
                continue;
            }

            map.put(v, offset);
            offset -= MIPSInstruction.WORD_SIZE;
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
            size += MIPSInstruction.WORD_SIZE;
        }
        // space for temporary variables (_tempVirt[0/1])
        size += 2 * MIPSInstruction.WORD_SIZE;

        // align to 8-byte boundary
        int align = 2 * MIPSInstruction.WORD_SIZE;
        int rem = size % align;
        if (rem != 0) size += align - rem;
        return size * 4;
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

    private static boolean isHeader(int i, List<IRInstruction> instrs){
        return (i == 0) || isLabel(instrs.get(i)) || (isBranch(instrs.get(i - 1)));
    }

    private List<List<IRInstruction>> getBasicBlocks(List<IRInstruction> func_instrs) {
        assert func_instrs.size() > 0;
        
        List<List<IRInstruction>> basic_blocks = new ArrayList<List<IRInstruction>>();

        // find the basic blocks by identifying headers
        List<IRInstruction> curr_block = new ArrayList<>();
        curr_block.add(func_instrs.get(0)); // add entry instr to first basic block
        for (int i = 1; i < func_instrs.size(); i++) {
            IRInstruction inst = func_instrs.get(i);

            if (!isHeader(i, func_instrs)) {
                curr_block.add(inst);
                continue;
            }

            basic_blocks.add(curr_block);
            
            curr_block = new ArrayList<>();
            curr_block.add(inst);
        }
        basic_blocks.add(curr_block);

        return basic_blocks;
    }


    private List<String> getReadOperands(IRInstruction instr) {
        List<String> ops = new ArrayList<>();
        
        switch (instr.opCode) {
            case ASSIGN:
                if (instr.operands.length == 2) { // variable assign (Tiger IR page 3)
                    ops.add(instr.operands[1].toString());
                } else { // array assign (Tiger IR page 5) (assume we're not allocating registers for this)
                    return ops;
                }
                break;
            case ADD: case SUB: case MULT: case DIV: case AND: case OR:
            case BREQ: case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ:
                ops.add(instr.operands[1].toString());
                ops.add(instr.operands[2].toString());
                break;
            case RETURN:
                ops.add(instr.operands[0].toString());
                break;
            case CALL:
                for (int i = 1; i < instr.operands.length; i++) {
                    ops.add(instr.operands[i].toString());
                }
                break;
            case CALLR:
                for (int i = 2; i < instr.operands.length; i++) {
                    IROperand temp = instr.operands[i];
                    ops.add(temp.toString());
                }
                break;
            case ARRAY_STORE:
                ops.add(instr.operands[0].toString());
                break;
            case ARRAY_LOAD:
            case LABEL:
            case GOTO:
                break;
        }

        return ops;
    }

    private Optional<String> getDestOperand(IRInstruction instr) {
        switch (instr.opCode) {
            case ASSIGN:
                if (instr.operands.length == 2) { // variable assign (Tiger IR page 3)
                    return Optional.of(instr.operands[0].toString());
                } else { // array assign (Tiger IR page 5) (assume we're not allocating registers for this)
                    return Optional.empty();
                }
            case ADD: case SUB: case MULT: case DIV: case AND: case OR:
            case BREQ: case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ:
            case RETURN:
            case CALLR:
            case ARRAY_LOAD:
                return Optional.of(instr.operands[0].toString());
            case CALL:
            case ARRAY_STORE:
            case LABEL:
            case GOTO:
        }
        return Optional.empty();
    }

    private Map<String, Integer> getLifetimes(List<IRInstruction> block) {
        Map<String, Integer> lifetimes = new HashMap<>();
        Set<String> live_vars = new HashSet<>();

        for (int i = block.size() - 1; i >= 0; i--) {
            IRInstruction instr = block.get(i);

            Optional<String> dest_operand = getDestOperand(instr);
            if (dest_operand.isPresent()) {
                live_vars.remove(dest_operand.get());
            }

            List<String> read_operands = getReadOperands(instr);
            for (String op : read_operands) {
                if (isNumeric(op)) {  // only care about virtual registers, not immediates
                    continue;
                }

                live_vars.add(op);

                if (!lifetimes.containsKey(op)) {
                    lifetimes.put(op, 1);
                }
            }

            for (Map.Entry<String, Integer> kvpair : lifetimes.entrySet()) {
                String op = kvpair.getKey();
                int curr_lifetime = kvpair.getValue();
                lifetimes.put(op, curr_lifetime + 1);
            }
        }

        return lifetimes;
    }

    List<Map.Entry<String, Integer>> sortLifetimes(Map<String, Integer> lifetimes) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(lifetimes.entrySet());
        entryList.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
        return entryList;
    }

    private Map<String, String> virtualRegToArchReg(List<IRInstruction> block) {
        Map<String, Integer> lifetimes = getLifetimes(block);
        List<Map.Entry<String, Integer>> sorted_lifetimes = sortLifetimes(lifetimes);

        Map<String, String> virt_reg_to_arch_reg = new HashMap<>();
        for (int i = 0; i < Math.min(sorted_lifetimes.size(), REGISTERS.size()); i++) {
            String arch_r = REGISTERS.get(i);
            String virt_reg = sorted_lifetimes.get(i).getKey();
            virt_reg_to_arch_reg.put(virt_reg, arch_r);
        }

        return virt_reg_to_arch_reg;
    }

    public List<String> instructionSelection(IRProgram program) {
        List<String> mips = new ArrayList<>();
        mips.add(".text");
        Collections.reverse(program.functions); // to print the main function first
        for (IRFunction fn : program.functions) {
            current_func = fn.name;

            mips.add(fn.name + ":");
            int offset = calculateStackAllocation(fn);

            // prologue
            mips.add("  move $fp, $sp");
            mips.add("  addi $sp, $sp, -" + offset);

            // Get virtual registers and push arguments into them
            Map<String, Integer> v_reg_to_off = virtualRegisterToOffset(fn, mips);
            List<String> arg_loads = loadArguments(fn, v_reg_to_off);
            mips.addAll(arg_loads);

            // translate IR instructions into MIPS
            List<List<IRInstruction>> basic_blocks = getBasicBlocks(fn.instructions);
            for (List<IRInstruction> block : basic_blocks) {
                
                // allocate architectural registers to virtual registers
                Map<String, String> v_reg_to_arch_reg = virtualRegToArchReg(block);
                List<String> load_regs = popRegisters(v_reg_to_arch_reg, v_reg_to_off);
                mips.addAll(load_regs);
                
                // translate instructions in block
                for (IRInstruction instr : block) {
                    List<List<String>> list = selectInstruction(instr, v_reg_to_off, v_reg_to_arch_reg, current_func, offset);
                    for (List<String> instruction : list){
                        mips.addAll(instruction);
                    }
                }

                // store new values of virtual registers
                List<String> store_regs = pushRegisters(v_reg_to_arch_reg, v_reg_to_off);
                mips.addAll(store_regs);
            }

            // epilogue
            if (fn.name.equals("main")) {
                mips.add("  li $v0, 10");
                mips.add("  syscall");
            }else{
                // addi $sp, $sp, 104
                // jr $ra
                mips.add("  addi $sp, $sp, " + offset);
                mips.add("  jr $ra");
            }
            mips.add("");
        }
        return mips;
    }

    public static void writeMipsToFile(List<String> lines, String path) throws IOException {
        Files.write(Paths.get(path), lines);
    }
}
