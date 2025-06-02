// Tiger.g4 – LL(1) Tiger grammar (clean, no lexer modes)
// ----------------------------------------------------
// File name **must** be Tiger.g4 (capital‑T) so the grammar name matches the file name.
// Place this file in your project root and regenerate with:
//   java -jar lib/antlr-4.12.0-complete.jar -Dlanguage=Java -o antlr_generated_tiger Tiger.g4

grammar Tiger; // <‑‑ grammar name matches file name

// =====================
// PARSER RULES
// =====================

program
    : expr EOF
    ;

// ---------- L‑values ----------

lvalue
    : ID ( '.' ID | '[' expr ']' )*
    ;

// ---------- Primary forms ----------

primaryExpr
    : NIL
    | INTLIT
    | STRLIT
    | funcCall
    | recordCreation
    | arrayCreation
    | lvalue
    | '(' exprSeq ')'
    | ifExp
    | whileExp
    | forExp
    | BREAK
    | letExp
    ;

funcCall
    : ID '(' ( expr ( ',' expr )* )? ')'
    ;

arrayCreation
    : ID '[' expr ']' OF expr
    ;

recordCreation
    : ID '{' ( ID '=' expr ( ',' ID '=' expr )* )? '}'
    ;

// ---------- Control structures ----------

ifExp
    : IF expr THEN expr ( ELSE expr )?
    ;

whileExp
    : WHILE expr DO expr
    ;

forExp
    : FOR ID ':=' expr TO expr DO expr
    ;

letExp
    : LET decList IN exprSeq END
    ;

// ---------- Expression sequences ----------

exprSeq
    : ( expr ( ';' expr )* )?
    ;

// ---------- Expression precedence ----------

expr
    : lvalue ':=' expr               # assignExpr
    | orExpr                         # orAsTop
    ;

orExpr
    : andExpr ( '|' andExpr )*
    ;

andExpr
    : compExpr ( '&' compExpr )*
    ;

compExpr
    : addExpr ( ( '=' | '<>' | '<' | '>' | '<=' | '>=' ) addExpr )?
    ;

addExpr
    : mulExpr ( ( '+' | '-' ) mulExpr )*
    ;

mulExpr
    : unaryExpr ( ( '*' | '/' ) unaryExpr )*
    ;

unaryExpr
    : '-' unaryExpr
    | primaryExpr
    ;

// ---------- Declarations ----------

decList
    : ( declaration )*
    ;

declaration
    : typeDec
    | varDec
    | funcDec
    ;

typeDec
    : TYPE ID '=' typeExp
    ;

typeExp
    : ID
    | '{' typeFields '}'
    | ARRAY OF ID
    ;

typeFields
    : ( ID ':' ID ( ',' ID ':' ID )* )?
    ;

varDec
    : VAR ID ( ':' ID )? ':=' expr
    ;

funcDec
    : FUNCTION ID '(' typeFields ')' ( ':' ID )? '=' expr
    ;

// =====================
// LEXER RULES
// =====================

// --- Keywords ---
IF       : 'if';
THEN     : 'then';
ELSE     : 'else';
WHILE    : 'while';
FOR      : 'for';
TO       : 'to';
DO       : 'do';
LET      : 'let';
IN       : 'in';
END      : 'end';
OF       : 'of';
BREAK    : 'break';
NIL      : 'nil';
FUNCTION : 'function';
VAR      : 'var';
TYPE     : 'type';
ARRAY    : 'array';

// --- Identifiers & literals ---
ID      : [A-Za-z] [A-Za-z0-9_]* ;
INTLIT  : [0-9]+ ;

STRLIT
    : '"' ( '\\' [abfnrtv]
            | '\\' [0-3][0-7][0-7]
            | '\\' 'x' [0-9A-Fa-f]{2}
            | '\\\\'
            | '\\"'
            | ~["\\]
            )* '"'
    ;

// --- Simple block comments (no nesting) ---
COMMENT
    : '/*' .*? '*/' -> skip
    ;

// --- Whitespace ---
WS  : [ \t\r\n]+ -> skip ;
