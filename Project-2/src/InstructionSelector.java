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
    private int pc = 1000;
    private int fp = 1000;

    /* 'temp' virtual registers for storing immediates
     *     Example 1:
     *     Tiger IR branch instructions allow immediates in conditionals (bgt x 2 label)
     *     MIPS branch instructions only take two registers (bgt x y label)
     *     => need to store immediate in a register
     * 
     *     Example 2:
     *     May need to load/store a memory address from/to index into an array
     *     Can use two register to calculate the address to read/write from/to
     */
    private static String _tempVirt0 = "temp0";
    private static String _tempVirt1 = "temp1";
    private static final Map<String, String> TEMPLATES = new HashMap<>();
    static {
        TEMPLATES.put("ADD",        "add ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("SUB",        "sub ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("MULT",       "MULT ${dst} ${lhs}, ${rhs}");
        TEMPLATES.put("DIV",        "DIV  ${dst} ${lhs}, ${rhs}");
        TEMPLATES.put("AND",        "AND ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("OR",         "OR  ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("GOTO",       "j    ${label}");
        TEMPLATES.put("BREQ",       "BEQ  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRNEQ",      "BNE  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLT",       "BLT  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGT",       "BGT  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLEQ",      "BLE  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGEQ",      "bge  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("ARRAY_LOAD", "LW   ${dst}, ${offset}(${base})");
        TEMPLATES.put("ARRAY_STORE","SW   ${src}, ${offset}(${base})");
        TEMPLATES.put("CALL",       "JAL  ${func}");
        TEMPLATES.put("CALLR",      "JAL  ${func}\nMOVE ${dst}, $v0");
        TEMPLATES.put("RETURN",     "JR   $ra");
        TEMPLATES.put("LABEL",      "${label}:");
        TEMPLATES.put("ASSIGN",     "lw ${dst}, ${src}");
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

    private static List<String> selectInstruction(IRInstruction instr) {
        String op = instr.opCode.name();
        String tpl = TEMPLATES.get(op);
        if (tpl == null) throw new IllegalArgumentException("Unmapped opcode: " + op);

        String dst   = "";
        String lhs   = "";
        String rhs   = "";
        String label = "";
        String func  = "";
        String base  = "";
        String src   = "";
        String offset= "";

        switch (instr.opCode) {
            case ADD: case MULT: case DIV:
            case AND: case OR:
                dst = instr.operands[0].toString();
                lhs = instr.operands[1].toString();
                rhs = instr.operands[2].toString();
                break;
            case SUB:
                dst = instr.operands[0].toString();
                lhs = instr.operands[1].toString();
                rhs = instr.operands[2].toString();
                if (isNumeric(lhs) || isNumeric(rhs)){
                    tpl = "addi ${dst}, ${lhs}, ${rhs}";
                    if (isNumeric(lhs)){
                        String temporary = rhs;
                        rhs = lhs;
                        lhs = temporary;
                    }
                }
                break;
            case ASSIGN:
                dst = instr.operands[0].toString();
                src = instr.operands[1].toString();
                break;
            case GOTO:
                label = instr.operands[0].toString();
                break;
            case BREQ:
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();

                break;
            case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ:
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();
                if (isNumeric(lhs) || isNumeric(rhs)){
                    tpl = "addi ${dst}, ${lhs}, ${rhs}";
                    if (isNumeric(lhs)){
                        
                    }
                }
                break;
            case ARRAY_LOAD:
                dst    = instr.operands[0].toString();
                base   = instr.operands[1].toString();
                offset = instr.operands[2].toString();
                break;
            case ARRAY_STORE:
                src    = instr.operands[0].toString();
                base   = instr.operands[1].toString();
                offset = instr.operands[2].toString();
                break;
            case CALL:
                func = instr.operands[0].toString();
                break;
            case CALLR:
                func = instr.operands[1].toString();
                dst  = instr.operands[0].toString();
                break;
            case RETURN:
                break;
            case LABEL:
                label = instr.operands[0].toString();
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
            lines.add("  " + filled);
        }
        return lines;
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


    public List<String> instructionSelection(IRProgram program) {
        List<String> mips = new ArrayList<>();
        for (IRFunction fn : program.functions) {
            mips.add(fn.name + ":");
            int offset = calculateStackAllocation(fn);
            this.fp = this.pc;
            this.pc += offset;
            mips.add("  move $fp, $sp");
            mips.add("  addi $sp, $sp, -" + offset);
            for (IRInstruction instr : fn.instructions) {
                mips.addAll(selectInstruction(instr));
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
