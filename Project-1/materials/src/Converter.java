import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.PrintWriter;

import ir.IRFunction;
import ir.IRInstruction;
import ir.IRProgram;
import ir.IRReader;
import ir.datatype.IRArrayType;
import ir.datatype.IRIntType;
import ir.operand.IROperand;
import ir.operand.IRVariableOperand;

public class Converter {

    public static IRProgram readProgram(String inputPath) throws Exception {
        IRReader irReader = new IRReader();
        return irReader.parseIRFile(inputPath);
    }

    public static void writeProgram(String outputPath, IRProgram program) throws Exception {
        PrintWriter writer = new PrintWriter(outputPath, "UTF-8");
        for (IRFunction function : program.functions) {
            writeFunction(writer, function);
        }
        writer.close();
    }

    private static void writeFunction(PrintWriter writer, IRFunction function) {
        writer.println("#start_function");

        // Print signature
        if (function.returnType == null)
            writer.print("void");
        else
            writer.print(function.returnType);
        writer.print(' ');
        writer.print(function.name);
        writer.print('(');
        boolean first = true;
        for (IRVariableOperand param : function.parameters) {
            if (first)
                first = false;
            else
                writer.print(", ");
            writer.print(param.type);
            writer.print(' ');
            writer.print(param.getName());
        }
        writer.println("):");

        // Print variable lists
        Set<String> paramNames = new HashSet<>();
        for (IRVariableOperand param : function.parameters)
            paramNames.add(param.getName());
        List<String> intList = new ArrayList<>();
        List<String> floatList = new ArrayList<>();
        for (IRVariableOperand variable : function.variables) {
            if (paramNames.contains(variable.getName()))
                continue;
            if (variable.type instanceof IRArrayType) {
                IRArrayType arrayType = (IRArrayType)variable.type;
                if (arrayType.getElementType() == IRIntType.get())
                    intList.add(String.format("%s[%d]", variable.getName(), arrayType.getSize()));
                else
                    floatList.add(String.format("%s[%d]", variable.getName(), arrayType.getSize()));
            } else {
                if (variable.type == IRIntType.get())
                    intList.add(variable.getName());
                else
                    floatList.add(variable.getName());
            }
        }
        writer.print("int-list: ");
        writer.println(String.join(", ", intList));
        writer.print("float-list: ");
        writer.println(String.join(", ", floatList));

        // Print instructions
        for (IRInstruction instruction : function.instructions) {
            if (instruction.opCode != IRInstruction.OpCode.LABEL)
                writer.print("    ");
            printInstruction(writer, instruction);
        }

        writer.println("#end_function");
    }

    private static void printInstruction(PrintWriter writer, IRInstruction instruction) {
        if (instruction.opCode == IRInstruction.OpCode.LABEL) {
            writer.print(instruction.operands[0]);
            writer.print(":");
            writer.println();
            return;
        }
        writer.print(instruction.opCode);
        for (IROperand operand : instruction.operands) {
            writer.print(", ");
            writer.print(operand);
        }
        writer.println();
    }
}
