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
        List<String> mips = InstructionSelector.instructionSelection(prog);
        InstructionSelector.writeMipsToFile(mips, "out.s");
        System.out.println("Generated " + mips.size() + " MIPS lines to out.s");
    }
}
