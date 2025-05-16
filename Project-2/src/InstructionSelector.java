import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import ir.IRException;
import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;
import ir.IRReader;

public class InstructionSelector {
    private static final Map<String, String> TEMPLATES = new HashMap<>();
    static {
        TEMPLATES.put("ADD",        "ADD ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("SUB",        "SUB ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("MULT",       "MULT ${dst} ${lhs}, ${rhs}");
        TEMPLATES.put("DIV",        "DIV  ${dst} ${lhs}, ${rhs}");
        TEMPLATES.put("AND",        "AND ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("OR",         "OR  ${dst}, ${lhs}, ${rhs}");
        TEMPLATES.put("GOTO",       "J    ${label}");
        TEMPLATES.put("BREQ",       "BEQ  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRNEQ",      "BNE  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLT",       "BLT  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGT",       "BGT  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRLEQ",      "BLE  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("BRGEQ",      "BGE  ${lhs}, ${rhs}, ${label}");
        TEMPLATES.put("ARRAY_LOAD", "LW   ${dst}, ${offset}(${base})");
        TEMPLATES.put("ARRAY_STORE","SW   ${src}, ${offset}(${base})");
        TEMPLATES.put("CALL",       "JAL  ${func}");
        TEMPLATES.put("CALLR",      "JAL  ${func}\nMOVE ${dst}, $v0");
        TEMPLATES.put("RETURN",     "JR   $ra");
        TEMPLATES.put("LABEL",      "${label}:");
        TEMPLATES.put("ASSIGN",     "MOVE ${dst}, ${src}");
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
            case ADD: case SUB: case MULT: case DIV:
            case AND: case OR:
                dst = instr.operands[0].toString();
                lhs = instr.operands[1].toString();
                rhs = instr.operands[2].toString();
                break;
            case ASSIGN:
                dst = instr.operands[0].toString();
                src = instr.operands[1].toString();
                break;
            case GOTO:
                label = instr.operands[0].toString();
                break;
            case BREQ: case BRNEQ: case BRLT: case BRGT: case BRLEQ: case BRGEQ:
                lhs   = instr.operands[1].toString();
                rhs   = instr.operands[2].toString();
                label = instr.operands[0].toString();
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
            lines.add(filled);
        }
        return lines;
    }

    private static String formatReg(String name) {
        if (name == null || name.isEmpty()) return "";
        if (name.matches("\\d+")) return name;  // immediates stay numeric
        if (name.startsWith("$")) return name;
        return "$" + name;
    }

    public static List<String> instructionSelection(IRProgram program) {
        List<String> mips = new ArrayList<>();
        for (IRFunction fn : program.functions) {
            mips.add(fn.name + ":");
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
        List<String> mips = instructionSelection(prog);
        writeMipsToFile(mips, "out.s");
        System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
    }
}
