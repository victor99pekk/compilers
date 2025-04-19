import java.io.PrintWriter;

import ir.IRProgram;

public class Optimizer {
    public static void Main(String args[]) throws Exception {
        String inputFile = args[0];
        String outputFile = args[1];

        // parse ir program
        IRProgram program = Converter.readProgram(inputFile);

        // optimize program
        ProgramCFG pcfg = new ProgramCFG(program);
        pcfg.applyMarkSweep();
        IRProgram optimizedProgram = pcfg.convertToIRProgram();

        // output optimizes ir program
        Converter.writeProgram(outputFile, optimizedProgram);
    }
}