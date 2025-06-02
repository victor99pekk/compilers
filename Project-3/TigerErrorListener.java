import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.IntervalSet;

public class TigerErrorListener extends BaseErrorListener {
    private boolean hasError = false;

    public boolean hasErrors() {
        return hasError;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, 
                             int line, int charPositionInLine, String msg, RecognitionException e) {
        hasError = true;  // record that an error occurred

        // Determine the offending token text
        String offendingText;
        if (offendingSymbol instanceof Token) {
            Token token = (Token) offendingSymbol;
            if (token.getType() == Token.EOF) {
                offendingText = "EOF";
            } else {
                offendingText = token.getText();
            }
        } else {
            // Lexer error (offendingSymbol is null for lexers:contentReference[oaicite:9]{index=9}): extract text from message
            // Message format for lexer errors: "token recognition error at: '...'"
            int start = msg.indexOf("'");
            int end = msg.lastIndexOf("'");
            if (start >= 0 && end >= start) {
                offendingText = msg.substring(start + 1, end);
            } else {
                offendingText = msg;  // fallback: use the whole message if we can't extract
            }
        }

        // Determine expected tokens
        String expectedTokens = "";
        if (recognizer instanceof org.antlr.v4.runtime.Parser) {
            Vocabulary vocab = ((org.antlr.v4.runtime.Parser) recognizer).getVocabulary();
            IntervalSet expectedTokenTypes;
            if (e != null) {
                // Use exception's expected token set if available
                expectedTokenTypes = e.getExpectedTokens();
            } else {
                // If exception is null (inline recovery), fetch from parser
                expectedTokenTypes = ((org.antlr.v4.runtime.Parser) recognizer).getExpectedTokens();
            }
            // Convert token types to token names or literals
            expectedTokens = formatTokenSet(expectedTokenTypes, vocab);
        }

        // Print the error message in required format
        if (expectedTokens.isEmpty()) {
            // No specific expected tokens (could be a lexer error with no context of expected tokens)
            System.out.println("Error: line " + line + ", unexpected token '" + offendingText + "'");
        } else {
            System.out.println("Error: line " + line + ", unexpected token '" + offendingText + "', expecting " + expectedTokens);
        }
    }

    /** Helper to format IntervalSet of expected tokens as a readable string. */
    private String formatTokenSet(IntervalSet types, Vocabulary vocab) {
        // Get each token name; join with " or "
        java.util.List<String> tokenNames = new java.util.ArrayList<>();
        for (int ttype : types.toArray()) {
            if (ttype == Token.EOF) {
                tokenNames.add("EOF");
            } else {
                String name = vocab.getSymbolicName(ttype);
                if (name == null || name.isEmpty()) {
                    // If no symbolic name, use literal name or display text
                    name = vocab.getLiteralName(ttype);
                    if (name == null) {
                        name = vocab.getDisplayName(ttype);
                    }
                }
                // Remove quotes around literal tokens for cleaner output
                if (name != null && name.startsWith("'") && name.endsWith("'")) {
                    name = name.substring(1, name.length() - 1);
                }
                tokenNames.add(name);
            }
        }
        if (tokenNames.isEmpty()) {
            return "";
        }
        if (tokenNames.size() == 1) {
            return tokenNames.get(0);
        }
        // Join multiple expected tokens with " or "
        return String.join(" or ", tokenNames);
    }
}
