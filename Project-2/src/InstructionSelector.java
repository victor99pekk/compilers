import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class InstructionSelector {
    private static final Map<String,String> TEMPLATES = new HashMap<>();
    static {
        TEMPLATES.put("ADD",   "ADD {dst}, {lhs}, {rhs}");
        TEMPLATES.put("SUB",   "SUB {dst}, {lhs}, {rhs}");
        TEMPLATES.put("MUL",   "MUL {dst}, {lhs}, {rhs}");
        TEMPLATES.put("DIV",   "DIV {lhs}, {rhs}\\nMFLO {dst}");  // DIV -> LO
        TEMPLATES.put("LOAD",  "LW  {dst}, {offset}({base})");
        TEMPLATES.put("STORE", "SW  {src}, {offset}({base})");
        TEMPLATES.put("CALL",  "JAL {funcLabel}");
        TEMPLATES.put("RET",   "JR  $ra");
    }

    public static class IRInstr {
        public String opcode;
        public String dst, lhs, rhs;
        public String base, funcLabel;
        public int    offset;
        public String src;
        public IRInstr(String op, String d, String l, String r) {
            opcode = op; dst = d; lhs = l; rhs = r;
        }
        public IRInstr(String op, String d, String b, int off, boolean isLoad) {
            opcode = op; dst = isLoad ? d : null;
            src    = isLoad ? null : d;
            base   = b; offset = off;
        }
        public IRInstr(String op, String f) {
            opcode = op; funcLabel = f;
        }
    }

    private static List<String> selectInstruction(IRInstr instr) {
        String tpl = TEMPLATES.get(instr.opcode);
        if (tpl == null) {
            throw new IllegalArgumentException("No template for " + instr.opcode);
        }
        String filled = tpl
            .replace("{dst}",       instr.dst     != null ? instr.dst       : "")
            .replace("{lhs}",       instr.lhs     != null ? instr.lhs       : "")
            .replace("{rhs}",       instr.rhs     != null ? instr.rhs       : "")
            .replace("{base}",      instr.base    != null ? instr.base      : "")
            .replace("{funcLabel}", instr.funcLabel != null ? instr.funcLabel : "")
            .replace("{offset}",    Integer.toString(instr.offset))
            .replace("{src}",       instr.src     != null ? instr.src       : "");
        return Arrays.asList(filled.split("\\\\n"));
    }

    public static List<String> instructionSelection(List<IRInstr> irProgram) {
        List<String> mips = new ArrayList<>();
        for (IRInstr instr : irProgram) {
            mips.addAll(selectInstruction(instr));
        }
        return mips;
    }

    public static void writeMipsToFile(List<String> mipsLines, String path) throws IOException {
        Files.write(Paths.get(path), mipsLines);
    }

    // public static void main(String[] args) throws IOException {
    //     // Suppose you already have parsed IR:
    //     List<IRInstr> ir = List.of(
    //         new IRInstr("ADD", "v1", "v2", "v3"),
    //         new IRInstr("LOAD", "v4", "v5", 8, true),
    //         new IRInstr("CALL", "myFunction"),
    //         new IRInstr("RET",  "")
    //     );

    //     List<String> mips = instructionSelection(ir);
    //     writeMipsToFile(mips, "out.s");

    //     System.out.println("Generated MIPS:");
    //     mips.forEach(System.out::println);
    // }
}
