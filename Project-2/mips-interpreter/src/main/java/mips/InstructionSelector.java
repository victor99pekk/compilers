// import java.io.IOException;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.*;

// import ir.IRException;
// import ir.IRFunction;
// import ir.IRInstruction;
// import ir.IRProgram;
// import ir.IRReader;
// import ir.datatype.IRArrayType;
// import ir.datatype.IRType;
// import ir.operand.IRVariableOperand;
// import ir.operand.IROperand;
// import main.java.mips.MIPSInstruction;
// import main.java.mips.MIPSOp;
// import main.java.mips.operand.Register;
// import main.java.mips.operand.Imm;
// import main.java.mips.operand.Addr;

// public class InstructionSelector {
//     public static void main(String[] args) throws IOException, IRException {
//         if (args.length != 1) {
//             System.err.println("Usage: InstructionSelector <irFile>");
//             System.exit(1);
//         }
//         translateIRToMIPS(args[0], "out.s");
//         System.out.println("Generated MIPS in out.s");
//     }

//     public static void translateIRToMIPS(String irFile, String outFile) throws IOException, IRException {
//         IRProgram prog = new IRReader().parseIRFile(irFile);
//         List<String> lines = new ArrayList<>();
//         for (IRFunction fn : prog.functions) {
//             lines.add(fn.name + ":");
//             // prologue
//             int frameSize = calculateStackAllocation(fn);
//             lines.add("  move $fp, $sp");
//             lines.add("  addi $sp, $sp, -" + frameSize);
//             lines.add("  sw   $fp, 0($sp)");
//             lines.add("  sw   $ra, " + MIPSInstruction.WORD_SIZE + "($sp)");
//             // load parameters into locals
//             for (int i = 0; i < fn.parameters.size(); i++) {
//                 String pname = fn.parameters.get(i).getName();
//                 if (i < 4) {
//                     lines.add("  move $" + pname + ", $a" + i);
//                 } else {
//                     int offset = MIPSInstruction.WORD_SIZE * (2 + (i - 4));
//                     lines.add("  lw   $" + pname + ", " + offset + "($fp)");
//                 }
//             }
//             // body
//             for (IRInstruction instr : fn.instructions) {
//                 IROperand[] ops = instr.operands;
//                 List<MIPSInstruction> lowers = lowerInstruction(instr);
//                 for (MIPSInstruction mi : lowers) {
//                     lines.add("  " + mi);
//                 }
//             }
//             // epilogue
//             lines.add("  lw   $fp, 0($sp)");
//             lines.add("  lw   $ra, " + MIPSInstruction.WORD_SIZE + "($sp)");
//             lines.add("  addi $sp, $sp, " + frameSize);
//             lines.add("  jr   $ra");
//             lines.add("");
//         }
//         Files.write(Paths.get(outFile), lines);
//     }

//     private static int calculateStackAllocation(IRFunction fn) {
//         int size = 2 * MIPSInstruction.WORD_SIZE; // save fp and ra
//         // locals
//         for (IRVariableOperand var : fn.variables) {
//             IRType type = var.type;
//             if (type instanceof IRArrayType) {
//                 int elems = ((IRArrayType) type).getSize();
//                 size += elems * MIPSInstruction.WORD_SIZE;
//             } else {
//                 size += MIPSInstruction.WORD_SIZE;
//             }
//         }
//         // align to 8 bytes
//         if (size % 8 != 0) size += 8 - (size % 8);
//         return size;
//     }

//     private static List<MIPSInstruction> lowerInstruction(IRInstruction instr) {
//         List<MIPSInstruction> out = new ArrayList<>();
//         IROperand[] ops = instr.operands;
//         switch (instr.opCode) {
//             case ADD:
//                 out.add(new MIPSInstruction(MIPSOp.ADD,
//                     new Register(ops[0].toString()),
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 break;
//             case SUB:
//                 out.add(new MIPSInstruction(MIPSOp.SUB,
//                     new Register(ops[0].toString()),
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 break;
//             case MULT:
//                 out.add(new MIPSInstruction(MIPSOp.MULT,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 out.add(new MIPSInstruction(MIPSOp.MFLO,
//                     new Register(ops[0].toString())));
//                 break;
//             case DIV:
//                 out.add(new MIPSInstruction(MIPSOp.DIV,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 out.add(new MIPSInstruction(MIPSOp.MFLO,
//                     new Register(ops[0].toString())));
//                 break;
//             case AND:
//                 out.add(new MIPSInstruction(MIPSOp.AND,
//                     new Register(ops[0].toString()),
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 break;
//             case OR:
//                 out.add(new MIPSInstruction(MIPSOp.OR,
//                     new Register(ops[0].toString()),
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString())));
//                 break;
//             case ASSIGN:
//                 out.add(new MIPSInstruction(MIPSOp.MOVE,
//                     new Register(ops[0].toString()),
//                     new Register(ops[1].toString())));
//                 break;
//             case GOTO:
//                 out.add(new MIPSInstruction(MIPSOp.J,
//                     new Imm(ops[0].toString())));
//                 break;
//             case BREQ:
//                 out.add(new MIPSInstruction(MIPSOp.BEQ,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case BRNEQ:
//                 out.add(new MIPSInstruction(MIPSOp.BNE,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case BRLT:
//                 out.add(new MIPSInstruction(MIPSOp.BLT,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case BRGT:
//                 out.add(new MIPSInstruction(MIPSOp.BGT,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case BRLEQ:
//                 out.add(new MIPSInstruction(MIPSOp.BLE,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case BRGEQ:
//                 out.add(new MIPSInstruction(MIPSOp.BGE,
//                     new Register(ops[1].toString()),
//                     new Register(ops[2].toString()),
//                     new Imm(ops[0].toString())));
//                 break;
//             case ARRAY_LOAD:
//                 int offL = Integer.parseInt(ops[2].toString()) * MIPSInstruction.WORD_SIZE;
//                 out.add(new MIPSInstruction(MIPSOp.LW,
//                     new Register(ops[0].toString()),
//                     new Addr(offL,
//                         new Register(ops[1].toString()))));
//                 break;
//             case ARRAY_STORE:
//                 int offS = Integer.parseInt(ops[2].toString()) * MIPSInstruction.WORD_SIZE;
//                 out.add(new MIPSInstruction(MIPSOp.SW,
//                     new Register(ops[0].toString()),
//                     new Addr(offS,
//                         new Register(ops[1].toString()))));
//                 break;
//             case CALL:
//                 out.add(new MIPSInstruction(MIPSOp.JAL,
//                     new Imm(ops[0].toString())));
//                 break;
//             case CALLR:
//                 out.add(new MIPSInstruction(MIPSOp.JAL,
//                     new Imm(ops[1].toString())));
//                 out.add(new MIPSInstruction(MIPSOp.MOVE,
//                     new Register(ops[0].toString()),
//                     new Register("v0")));
//                 break;
//             case RETURN:
//                 out.add(new MIPSInstruction(MIPSOp.JR,
//                     new Register("ra")));
//                 break;
//             case LABEL:
//                 out.add(new MIPSInstruction(MIPSOp.LABEL,
//                     new Imm(ops[0].toString())));
//                 break;
//             default:
//                 throw new IllegalArgumentException("Unsupported opcode: " + instr.opCode);
//         }
//         return out;
//     }
// }
