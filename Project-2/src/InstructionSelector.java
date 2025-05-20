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

    // private static void storeNumeric(List<List<String>>list, String dst, String src){
    //     String tpl = "addi ${dst}, ${src}, $0";
    //     String lhs   = "";
    //     String rhs   = "";
    //     String label = "";
    //     String func  = "";
    //     String base  = "";
    //     String offset= "";
    //     List<String> lines = new ArrayList<>();
    //     for (String line : tpl.split("\\n")) {
    //         String filled = line
    //             .replace("${dst}",   formatReg(dst))
    //             .replace("${lhs}",   formatReg(lhs))
    //             .replace("${rhs}",   formatReg(rhs))
    //             .replace("${label}", label)
    //             .replace("${func}",  func)
    //             .replace("${base}",  formatReg(base))
    //             .replace("${src}",   formatReg(src))
    //             .replace("${offset}", offset);
    //         lines.add("  " + filled);
    //     }
    //     list.add(lines);
    // }

    // private static void sw(List<List<String>>list, String dst, String src){
    //     String tpl = "sw ${dst}, $0, ${src}";
    //     String lhs   = "";
    //     String rhs   = "";
    //     String label = "";
    //     String func  = "";
    //     String base  = "";
    //     String offset= "";
    //     List<String> lines = new ArrayList<>();
    //     for (String line : tpl.split("\\n")) {
    //         String filled = line
    //             .replace("${dst}",   formatReg(dst))
    //             .replace("${lhs}",   formatReg(lhs))
    //             .replace("${rhs}",   formatReg(rhs))
    //             .replace("${label}", label)
    //             .replace("${func}",  func)
    //             .replace("${base}",  formatReg(base))
    //             .replace("${src}",   formatReg(src))
    //             .replace("${offset}", offset);
    //         lines.add("  " + filled);
    //     }
    //     list.add(lines);
    // }

    // private static void addi(List<List<String>>list, String dst, String rhs, String lhs){
    //     String tpl = "addi ${dst}, $0, ${src}";
    //     String src   = "";
    //     String label = "";
    //     String func  = "";
    //     String base  = "";
    //     String offset= "";
    //     List<String> lines = new ArrayList<>();
    //     for (String line : tpl.split("\\n")) {
    //         String filled = line
    //             .replace("${dst}",   formatReg(dst))
    //             .replace("${lhs}",   formatReg(lhs))
    //             .replace("${rhs}",   formatReg(rhs))
    //             .replace("${label}", label)
    //             .replace("${func}",  func)
    //             .replace("${base}",  formatReg(base))
    //             .replace("${src}",   formatReg(src))
    //             .replace("${offset}", offset);
    //         lines.add("  " + filled);
    //     }
    //     list.add(lines);
    // }

    // Move from architectural register into virtual register
    private static void storeVirtualRegister(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off)
    {
        int offset = v_reg_to_off.get(virtReg);
        createLines(list, "sw ${src}, ${offset}(${base})","","","","","","$fp", archReg, Integer.toString(offset));
    }

    // Move from virtual register into architectural register
    private void loadVirtualRegister(
        List<List<String>> list,
        String archReg,
        String virtReg,
        Map<String, Integer> v_reg_to_off)
    {
        int offset = v_reg_to_off.get(virtReg);
        createLines(list, "lw ${dst}, ${offset}(${base})", archReg,"","","","","$fp", "", Integer.toString(offset));
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

    private void arithAndLogicInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
        String lhs  = instr.operands[1].toString();
        String rhs  = instr.operands[2].toString();
        String dst = instr.operands[0].toString();
        
        // If operand is immediate, load it into an architectural register and use that register
        // Otherwise, the operand is a virtual register and must be loaded into a physical register
        if (isNumeric(rhs)){
            li(list, _default_rhs, rhs);
        } else {
            loadVirtualRegister(list, _default_rhs, rhs, v_reg_to_off);
        }

        if (isNumeric(lhs)){
            li(list, _default_lhs, lhs);
        } else {
            loadVirtualRegister(list, _default_lhs, lhs, v_reg_to_off);
        }
        
        // Create mips version of the Tiger IR instruction
        String op = arithTigerOpToMipsOp(instr.opCode);
        createLines(list, "${label} ${dst}, ${lhs}, ${rhs}", _default_dest, _default_lhs, _default_rhs, op,"","","","");

        // Move result into virtual register
        storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
    }

    private void assignInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
        String dst = instr.operands[0].toString();
        String src = instr.operands[1].toString();
        
        // If operand is immediate, then it's essentially just a load immediate instruction
        if (isNumeric(src)) {
            li(list, _default_dest, src);
            storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
            return;
        }

        // Otherwise, move between registers
        loadVirtualRegister(list, _default_lhs, src, v_reg_to_off);
        createLines(list, "move ${dst}, ${src}", _default_dest, "", "", "", "", "", _default_lhs, "");
        storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
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

    private void branchInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, IRInstruction instr, String func_name) {
        String lhs   = instr.operands[1].toString();
        String rhs   = instr.operands[2].toString();
        
        String label = instr.operands[0].toString();
        String local_label = label + "_" + func_name;
        
        // If operand is immediate, load it into an architectural register and use that register
        // Otherwise, the operand is a virtual register and must be loaded into a physical register
        if (isNumeric(lhs)){
            li(list, _default_lhs, lhs);
        } else {
            loadVirtualRegister(list, _default_lhs, lhs, v_reg_to_off);
        }
        if (isNumeric(rhs)){
            li(list, _default_rhs, rhs);
        } else {
            loadVirtualRegister(list, _default_rhs, rhs, v_reg_to_off);
        }

        String op = tigerBranchOpToMipsBranchOps(instr.opCode);
        createLines(list, "${label} ${lhs}, ${rhs}, ${offset}", "", _default_lhs, _default_rhs, op, "", "", "", local_label);
    }

    private void arrayLoadInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
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
            loadVirtualRegister(list, _default_lhs, offset, v_reg_to_off);
        }
        // _default_rhs = base
        loadVirtualRegister(list, _default_rhs, base, v_reg_to_off);
        // _default_lhs <<= 2
        createLines(list, "sll ${dst}, ${lhs}, 2", _default_lhs, _default_lhs, "", "", "", "", "", "");
        // _default_lhs += base    # address = base + offset*4
        createLines(list, "add ${dst}, ${lhs}, ${rhs}", _default_lhs, _default_lhs, _default_rhs, "", "", "", "", "");

        // lw dst, 0($t)          # load word from address
        createLines(list, "lw ${dst}, 0(${base})", _default_dest, "", "", "", "", _default_lhs, "", "");
        // store into virtual register
        storeVirtualRegister(list, _default_dest, dst, v_reg_to_off);
    }

    private void saveRegisters(List<List<String>> list, int num_params) {
        // allocate space for arg registers to be saved
        createLines(list, "addi $sp, $sp, -16","","","","","","","","");
        
        // save arg registers
        for (int i = 0; i < num_params; i++) {
            String arg_reg = "$a" + i;
            String offset = Integer.toString(i * MIPSInstruction.WORD_SIZE);
            createLines(list, "sw ${src}, ${offset}($sp)","","","","","","",arg_reg,offset);
        }
    }

    private void allocateSpaceOnStack(List<List<String>> list, int size) {
        // allocate space for local variables
        createLines(list, "addi $sp, $sp, -{size}","","","","","","","","");
    }

    private void restoreRegisters(List<List<String>> list, int num_params) {
        // restore arg registers
        for (int i = 0; i < num_params; i++) {
            String arg_reg = "$a" + i;
            String offset = Integer.toString(i * MIPSInstruction.WORD_SIZE);
            createLines(list, "lw ${dst}, ${offset}($sp)",arg_reg,"","","","","","",offset);
        }
        createLines(list, "addi $sp, $sp, 16","","","","","","","","");
    }


        /**
     * Generate code for “assign, array, size, value”
     *   -> set every element of the array to the given value.
     *
     * Registers used:
     *   $t0 (_default_dest) : value to store
     *   $t1 (_default_lhs)  : loop counter i
     *   $t2 (_default_rhs)  : base address of array
     *   $t3                 : upper-bound N   (only if N is not immediate)
     *   $t4                 : scratch – holds effective address base+i*4
     */
    private void arrayStoreVal(List<List<String>> list,
                            Map<String,Integer> v_reg_to_off,
                            IRInstruction instr)
    {
        String array = instr.operands[0].toString();   // destination array
        String size  = instr.operands[1].toString();   // number of elements
        String value = instr.operands[2].toString();   // value to write

        /* 1.  load base pointer of the array  →  $t2 */
        loadVirtualRegister(list, _default_rhs, array, v_reg_to_off);          // $t2 ← base

        /* 2.  load / compute the value to be stored → $t0 */
        if (isNumeric(value))
            li(list, _default_dest, value);                                     // li  $t0, imm
        else
            loadVirtualRegister(list, _default_dest, value, v_reg_to_off);      // lw  $t0, …

        /* 3.  counter i ← 0  ( $t1 ) */
        li(list, _default_lhs, "0");                                        // li  $t1, 0

        /* 4.  prepare loop labels */
        String loopLbl = "arr_init_loop_" + current_func + "_" + array;
        String doneLbl = loopLbl + "_done";

        /* loopLbl: */
        list.add(List.of(loopLbl + ":"));

        /* if (i >= N)  goto doneLbl */
        if (isNumeric(size)) {
            li(list, "$t3", size);                                              // t3 ← N
            createLines(list, "bge ${lhs}, ${rhs}, ${label}",
                        "", _default_lhs, "$t3", doneLbl, "", "", "", "");
        } else {
            loadVirtualRegister(list, "$t3", size, v_reg_to_off);               // t3 ← N
            createLines(list, "bge ${lhs}, ${rhs}, ${label}",
                        "", _default_lhs, "$t3", doneLbl, "", "", "", "");
        }

        /* addr = base + (i << 2)  (use $t4) */
        createLines(list, "sll $t4, ${lhs}, 2", "", _default_lhs, "", "", "", "", "", "");
        createLines(list, "add $t4, $t4, ${rhs}", "", "", _default_rhs, "", "", "", "", "");

        /* store value */
        createLines(list, "sw ${src}, 0(${base})",
                    "", "", "", "", "", "$t4", _default_dest, "");

        // loadVirtualRegister(list, _default_dest, dst, v_reg_to_off);

        // // sw src, 0($t)          # store word to address
        // // offset = offset;
        // createLines(list, "sw ${dst}, 0(${base})", _default_dest, "", "", "", "", _default_lhs, "", "");
        /* i += 1 */
        createLines(list, "addi ${dst}, ${src}, 1",
                    _default_lhs, _default_lhs, "", "", "", "", "", "");

        /* jump back to loop */
        createLines(list, "j ${label}", "", "", "", loopLbl, "", "", "", "");

        /* doneLbl: */
        list.add(List.of(doneLbl + ":"));
    }


    private void arrayStoreInstr(List<List<String>>list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
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
            loadVirtualRegister(list, _default_lhs, offset, v_reg_to_off);
        }
        // _default_rhs = base
        loadVirtualRegister(list, _default_rhs, base, v_reg_to_off);
        // _default_lhs <<= 2
        createLines(list, "sll ${dst}, ${lhs}, 2", _default_lhs, _default_lhs, "", "", "", "", "", "");
        // _default_lhs += base    # address = base + offset*4
        createLines(list, "add ${dst}, ${lhs}, ${rhs}", _default_lhs, _default_lhs, _default_rhs, "", "", "", "", "");
        // _default_dst = src (using _default_dest, for lack of a better name)
        if (isNumeric(src)) {
            li(list, _default_dest, src);
        } else {
            loadVirtualRegister(list, _default_dest, src, v_reg_to_off);
        }

        // sw src, 0($t)          # store word to address
        // offset = offset;
        createLines(list, "sw ${dst}, 0(${base})", _default_dest, "", "", "", "", _default_lhs, "", "");
    }

    // private void saveRegisters(List<List<String>> list, int num_params) {
    //     // allocate space for arg registers to be saved
    //     createLines(list, "addi $sp, $sp, -16","","","","","","","","");
        
    //     // save arg registers
    //     for (int i = 0; i < num_params; i++) {
    //         String arg_reg = "$a" + i;
    //         String offset = Integer.toString(i * MIPSInstruction.WORD_SIZE);
    //         createLines(list, "sw ${src}, ${offset}($sp)","","","","","","",arg_reg,offset);
    //     }
    // }

    // private void restoreRegisters(List<List<String>> list, int num_params) {
    //     // restore arg registers
    //     for (int i = 0; i < num_params; i++) {
    //         String arg_reg = "$a" + i;
    //         String offset = Integer.toString(i * MIPSInstruction.WORD_SIZE);
    //         createLines(list, "lw ${dst}, ${offset}($sp)",arg_reg,"","","","","","",offset);
    //     }
    //     createLines(list, "addi $sp, $sp, 16","","","","","","","","");
    // }

    private void callInstr(List<List<String>> list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
        String func = instr.operands[0].toString();

        // Put values into arg registers
        for (int i = 1; i < instr.operands.length; i++) {
            String operand = instr.operands[i].toString();
            String arg_reg = "$a" + Integer.toString(i - 1);

            if (isNumeric(operand)){ // If offset is numeric, store it in a temp register
                li(list, arg_reg, operand);
            } else {
                loadVirtualRegister(list, arg_reg, operand, v_reg_to_off);
            }
        }

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
    }

    private void callrInstr(List<List<String>> list, Map<String, Integer> v_reg_to_off, IRInstruction instr) {
        String func = instr.operands[1].toString();
        String ret = instr.operands[0].toString();

        // Put values into arg registers
        for (int i = 2; i < instr.operands.length; i++) {
            String operand = instr.operands[i].toString();
            String arg_reg = "$a" + Integer.toString(i - 2);

            if (isNumeric(operand)){ // If offset is numeric, store it in a temp register
                li(list, arg_reg, operand);
            } else {
                loadVirtualRegister(list, arg_reg, operand, v_reg_to_off);
            }
        }

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

        // ret = $v0  # store return value in virtual register
        storeVirtualRegister(list, "$v0", ret, v_reg_to_off);
    }

    private void getSyscalls(List<List<String>>list, Map<String, Integer> v_reg_to_off, String func, String dst) {
        // createLines(list,  "addi $sp, $sp, -4", "", "", "", "", "", "", "", "");
        // createLines(list,  "sw $v0, 0($sp)", "", "", "", "", "", "", "", "");
        
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

        // createLines(list,  "lw $v0, 0($sp)", "", "", "", "", "", "", "", "");
        // createLines(list,  "addi $sp, $sp, 4", "", "", "", "", "", "", "", "");

        storeVirtualRegister(list, "$v0", dst, v_reg_to_off);
    }

    private void putSyscalls(List<List<String>>list, Map<String, Integer> v_reg_to_off, String func, String arg) {
        // createLines(list,  "addi $sp, $sp, -8", "", "", "", "", "", "", "", "");
        // createLines(list,  "sw $v0, 4($sp)", "", "", "", "", "", "", "", "");
        
        if (func.equals("puti")) {
            // createLines(list,  "sw $a0, 0($sp)", "", "", "", "", "", "", "", "");

            createLines(list, "li $v0, 1", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $a0, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                loadVirtualRegister(list, "$a0", arg, v_reg_to_off);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
            
            // createLines(list,  "lw $a0, 0($sp)", "", "", "", "", "", "", "", "");
        }
        else if (func.equals("putf")) {
            // createLines(list,  "sw $f12, 0($sp)", "", "", "", "", "", "", "", "");

            createLines(list, "li $v0, 2", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $f12, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                loadVirtualRegister(list, "$f12", arg, v_reg_to_off);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");

            // createLines(list,  "lw $f12, 0($sp)", "", "", "", "", "", "", "", "");
        }
        else if (func.equals("putc")) {
            // createLines(list,  "sw $a0, 0($sp)", "", "", "", "", "", "", "", "");

            createLines(list, "li $v0, 11", "", "", "", "", "", "", "", "");
            if (isNumeric(arg)) {
                createLines(list, "li $a0, ${src}", "", "", "", "", "", "", arg, "");
            } else {
                loadVirtualRegister(list, "$a0", arg, v_reg_to_off);
            }
            createLines(list, "syscall", "", "", "", "", "", "", "", "");
            
            // createLines(list,  "lw $a0, 0($sp)", "", "", "", "", "", "", "", "");
        }

        // createLines(list,  "lw $v0, 4($sp)", "", "", "", "", "", "", "", "");
        // createLines(list,  "addi $sp, $sp, 8", "", "", "", "", "", "", "", "");
    }

    private List<List<String>> selectInstruction(IRInstruction instr, Map<String, Integer> v_reg_to_off, String func_name, int spOffset) {
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


        // Special instructions
        if (instr.opCode == IRInstruction.OpCode.LABEL) {
            createLines(list, tpl, dst, lhs, rhs, instr.operands[0].toString(), func, base, src, offset);
            return List.of(List.of(new StringBuilder(instr.operands[0].toString()).append("_").append(this.current_func).append(":").toString()));
        }
        if (instr.opCode == IRInstruction.OpCode.CALLR){
            func = instr.operands[1].toString();
            dst  = instr.operands[0].toString();  
            if (func.equals("geti") || func.equals("getf") || func.equals("getc")) {
                getSyscalls(list, v_reg_to_off, func, dst);
                return list;
            }
        }
        if (instr.opCode == IRInstruction.OpCode.CALL) {
            func = instr.operands[0].toString();
            lhs = instr.operands[1].toString();
            if (func.equals("puti") || func.equals("putf") || func.equals("putc")) {
                putSyscalls(list, v_reg_to_off, func, lhs);
                return list;
            }
        }

        switch (instr.opCode) {
            case ADD: case SUB: case MULT: case DIV: case AND: case OR:
                arithAndLogicInstr(list, v_reg_to_off, instr);
                return list;
            case ASSIGN:
                if (instr.operands.length == 2) {
                    /* simple x := y  */
                    assignInstr(list, v_reg_to_off, instr);

                } else if (instr.operands.length == 3) {
                    /* array-initialisation  A, N, v  */
                    arrayStoreVal(list, v_reg_to_off, instr);

                } else {
                    createLines(list, "error", dst, lhs, rhs, label, func, base, src, offset);
                }
                return list;
            case GOTO:
                label = instr.operands[0].toString();
                String local_label = label + "_" + func_name;
                createLines(list, "j ${label}", "", "", "", local_label, "", "", "", "");
                return list;
            case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ: case BREQ:
                branchInstr(list, v_reg_to_off, instr, func_name);
                return list;
            case ARRAY_LOAD:
                arrayLoadInstr(list, v_reg_to_off, instr);
                return list;
            case ARRAY_STORE:
                arrayStoreInstr(list, v_reg_to_off, instr);
                return list;
            case CALL:
                callInstr(list, v_reg_to_off, instr);
                return list;
            case CALLR:
                // func = instr.operands[1].toString();
                // dst  = instr.operands[0].toString();
                // // use thomas' code to call function -> created callrInstr because is callInstr isn't completely reusable
                callrInstr(list, v_reg_to_off, instr);
                return list;
            case RETURN:
                String retval = instr.operands[0].toString();
                if (isNumeric(retval)){ // If offset is numeric, store it in a temp register
                    li(list, "$v0", retval);
                } else {
                    loadVirtualRegister(list, "$v0", retval, v_reg_to_off);
                }
                createLines(list, "addi $sp, $sp, ${offset}","","","","","","","",Integer.toString(spOffset));
                createLines(list, "jr $ra", dst, lhs, rhs, label, func, base, src, offset);
                return list;

            default:
                throw new IllegalArgumentException("Unsupported opcode: " + op);
        }

        // List<String> lines = new ArrayList<>();
        // for (String line : tpl.split("\\n")) {
        //     String filled = line
        //         .replace("${dst}",   formatReg(dst))
        //         .replace("${lhs}",   formatReg(lhs))
        //         .replace("${rhs}",   formatReg(rhs))
        //         .replace("${label}", label)
        //         .replace("${func}",  func)
        //         .replace("${base}",  formatReg(base))
        //         .replace("${src}",   formatReg(src))
        //         .replace("${offset}", offset);
        //     if (is_label) {
        //         filled = new StringBuilder(filled).append(":").toString();
        //     }else{
        //         lines.add("  " + filled);
        //     }
        // }
        // list.add(lines);
        // return list;
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

        // The rest of the arguments are on the stack and need to be loaded in the function prologue
        // TODO: HANDLE MORE THAN 4 ARGUMENTS

        return get_args;
    }


    private static String formatReg(String name) {
        if (name == null || name.isEmpty()) return "";
        // if (name.matches("\\d+")) return name;  // immediates stay numeric
        if (isNumeric(name)) return name;
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
            // IRType type = var.type;
            // if (type instanceof IRArrayType) {
            //     int elems = ((IRArrayType) type).getSize();
            //     size += elems * MIPSInstruction.WORD_SIZE;
            // } else {
            size += MIPSInstruction.WORD_SIZE;
            // }
        }
        // space for temporary variables (_tempVirt[0/1])
        size += 2 * MIPSInstruction.WORD_SIZE;

        // align to 8-byte boundary
        int align = 2 * MIPSInstruction.WORD_SIZE;
        int rem = size % align;
        if (rem != 0) size += align - rem;
        return size * 4;
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
            // this.fp = this.pc;
            // this.pc += offset;
            mips.add("  move $fp, $sp");
            mips.add("  addi $sp, $sp, -" + offset);

            // Get virtual registers and push arguments into them
            Map<String, Integer> v_reg_to_off = virtualRegisterToOffset(fn, mips);
            List<String> arg_loads = loadArguments(fn, v_reg_to_off);
            mips.addAll(arg_loads);

            for (IRInstruction instr : fn.instructions) {
                List<List<String>> list = selectInstruction(instr, v_reg_to_off, current_func, offset);
                for (List<String> instruction : list){
                    mips.addAll(instruction);
                }
            }
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

    // public static void main(String[] args) throws IOException, IRException {
    //     IRProgram prog = new IRReader().parseIRFile(args[0]);
    //     InstructionSelector is = new InstructionSelector();
    //     List<String> mips = is.instructionSelection(prog);
    //     // List<String> mips = instructionSelection(prog);
    //     writeMipsToFile(mips, "out.s");
    //     System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
    // }
}
