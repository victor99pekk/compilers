import java.io.IOException;
import java.util.List;

import ir.IRException;
import ir.IRProgram;
import ir.IRReader;

public class Main {
    
    public static void main(String[] args) throws IRException, IOException {
        // convert the IR code into MIPS
        // write a MIPS file

        IRProgram prog = new IRReader().parseIRFile(args[0]);
        if (args[1].equals("--naive")) {
            InstructionSelector is = new InstructionSelector();
            List<String> mips = is.instructionSelection(prog);
            InstructionSelector.writeMipsToFile(mips, "out.s");
            System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
        } else {
            IntraBlockAllocInstructionSelector is = new IntraBlockAllocInstructionSelector();
            List<String> mips = is.instructionSelection(prog);
            IntraBlockAllocInstructionSelector.writeMipsToFile(mips, "out.s");
            System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
        }
    }
}
