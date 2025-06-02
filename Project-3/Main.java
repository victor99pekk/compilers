import org.antlr.v4.runtime.*;          // CharStreams, CommonTokenStream, etc.
import org.antlr.v4.runtime.tree.*;     // Optional: if you ever need the tree

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: Main <file.tiger>");
            return;
        }

        // 1. Read input file
        CharStream input = CharStreams.fromFileName(args[0]);

        // 2. Create lexer and parser generated from Tiger.g4
        TigerLexer lexer = new TigerLexer(input);
        TigerParser parser = new TigerParser(new CommonTokenStream(lexer));

        // 3. Add custom error listener
        TigerErrorListener err = new TigerErrorListener();
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        lexer.addErrorListener(err);
        parser.addErrorListener(err);

        // 4. Invoke the top-level rule in the grammar
        parser.tigerProgram();      // start rule is 'program'

        // 5. Print result
        if (!err.hasErrors()) {
            System.out.println("successful");
        }
    }
}
