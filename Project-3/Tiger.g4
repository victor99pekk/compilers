// Tiger.g4 – ANTLR4 grammar matching Project 3 specification
// ----------------------------------------------------
// File name **must** be Tiger.g4 so the grammar name matches the file name.

grammar Tiger;

// =====================
// PARSER RULES
// =====================

// Entry point of the program
tigerProgram
    : MAIN LET declarationSegment IN BEGIN statSeq END
    ;

declarationSegment
    : (declaration)*
    ;

declaration
    : varDeclaration
    | functDeclaration
    ;

varDeclaration
    : idList ':' type (ASSIGN literal)?
    ;

functDeclaration
    : FUNCTION ID '(' paramList? ')' ':' type BEGIN statSeq END
    ;

paramList
    : param (',' param)*
    ;

param
    : ID ':' type
    ;

type
    : INTEGER
    | FLOAT
    ;

idList
    : ID (',' ID)*
    ;

statSeq
    : (statement)*
    ;

statement
    : assignStat
    | ifStat
    | whileStat
    | forStat
    | funcCallStat
    | returnStat
    ;

assignStat
    : ID ASSIGN expr
    ;

ifStat
    : IF expr THEN statSeq (ELSE statSeq)? ENDIF
    ;

whileStat
    : WHILE expr DO statSeq ENDDO
    ;

forStat
    : FOR ID ASSIGN expr TO expr DO statSeq ENDDO
    ;

funcCallStat
    : ID '(' exprList? ')'
    ;

returnStat
    : RETURN expr
    ;

exprList
    : expr (',' expr)*
    ;

expr
    : expr ('*'|'/') expr      # MulDivExpr
    | expr ('+'|'-') expr      # AddSubExpr
    | '(' expr ')'             # ParensExpr
    | ID                       # IdExpr
    | literal                  # LiteralExpr
    | funcCallStat             # FuncCallExpr
    ;

literal
    : INTLIT
    | FLOATLIT
    ;

// =====================
// LEXER RULES
// =====================

// Keywords
MAIN    : 'main';
LET     : 'let';
IN      : 'in';
BEGIN   : 'begin';
END     : 'end';
ENDIF   : 'endif';
WHILE   : 'while';
DO      : 'do';
ENDDO   : 'enddo';
FOR     : 'for';
TO      : 'to';
IF      : 'if';
THEN    : 'then';
ELSE    : 'else';
RETURN  : 'return';
INTEGER : 'int';
FLOAT   : 'float';
FUNCTION: 'function';

// Literals
INTLIT     : [0-9]+;
FLOATLIT   : [0-9]+ '.' [0-9]+;
ID         : [a-zA-Z_] [a-zA-Z0-9_]*;

// Operators and symbols
ASSIGN : ':=';

// Punctuation
LPAREN : '(';
RPAREN : ')';
COMMA  : ',';
COLON  : ':';
SEMICOLON : ';';

// Whitespace and comments
WS      : [ \t\r\n]+ -> skip;
COMMENT : '/*' .*? '*/' -> skip;
LINE_COMMENT : '//' ~[\r\n]* -> skip;
