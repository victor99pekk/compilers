import ir.IRProgram;

public class Optimizer {
    public static void main(String[] args) throws Exception {
        String inputFile = args[0];
        String outputFile = "out.ir";

        // parse ir program
        IRProgram program = Converter.readProgram(inputFile);

        // optimize program
        ProgramCFG pcfg = new ProgramCFG(program);
        pcfg.findReachingDefs();
        pcfg.applyMarkSweep();
        IRProgram optimizedProgram = pcfg.convertToIRProgram();

        // output optimizes ir program
        Converter.writeProgram(outputFile, optimizedProgram);

        // // test input and output
        // IRProgram program = Converter.readProgram(inputFile);
        // Converter.writeProgram(outputFile, program);
    }
}