// Tiger.g4 – LL(1) grammar matching Appendix A of Project‑3.pdf
// ------------------------------------------------------------------
//  * Save exactly as Tiger.g4 (capital T).
//  * Combined lexer + parser grammar for ANTLR 4.x (Java target).
//  * All rules end with ONE semicolon; no duplicates.

grammar Tiger;

// ==================================================================
// PARSER RULES
// ==================================================================

// <tiger‑program> ::= main let <declaration‑segment> in begin <stat‑seq> end EOF

tigerProgram
    : MAIN LET declarationSegment IN BEGIN statSeq END EOF
    ;

// ------------------- Declaration segment --------------------------

declarationSegment
    : varDeclarationList functDeclarationList
    ;

varDeclarationList
    : /* empty */
    | varDeclaration varDeclarationList
    ;

varDeclaration
    : VAR idList COLON type optionalInit SEMICOLON
    ;

optionalInit
    : /* empty */
    | ASSIGN constant
    ;

functDeclarationList
    : /* empty */
    | functDeclaration functDeclarationList
    ;

functDeclaration
    : FUNCTION ID LPAREN paramList? RPAREN retType BEGIN statSeq END
    ;

paramList
    : param ( COMMA param )*
    ;

param
    : ID COLON type
    ;

retType
    : /* empty */
    | COLON type
    ;

// ------------------- Types ----------------------------------------

type
    : typeId
    | ARRAY LBRACK INTLIT RBRACK OF typeId
    ;

typeId
    : INTEGER
    | FLOATTY
    ;

idList
    : ID ( COMMA ID )*
    ;

// ------------------- Statement sequence ---------------------------

statSeq
    : ( stat )+
    ;

// ------------------- Individual statements ------------------------

stat
    : lvalue ASSIGN expr SEMICOLON                               # assignStat
    | IF expr THEN statSeq ENDIF SEMICOLON                        # ifNoElse
    | IF expr THEN statSeq ELSE statSeq ENDIF SEMICOLON           # ifWithElse
    | WHILE expr DO statSeq ENDDO SEMICOLON                       # whileStat
    | FOR ID ASSIGN expr TO expr DO statSeq ENDDO SEMICOLON       # forStat
    | funcCallWithOptPrefix SEMICOLON                             # funcCallStat
    | BREAK SEMICOLON                                             # breakStat
    | RETURN expr SEMICOLON                                       # returnStat
    | LET declarationSegment IN statSeq END                       # letBlockStat
    ;

// optional "lvalue :=" prefix used in function calls
optPrefix
    : lvalue ASSIGN
    ;

funcCallWithOptPrefix
    : optPrefix? ID LPAREN exprList? RPAREN
    ;

// ------------------- Expression list ------------------------------

exprList
    : expr ( COMMA expr )*
    ;

// ------------------- Expressions & precedence ---------------------
// order: ( ) > * / > + - > comparisons > & > |
// All binary operators are right‑associative.

expr
    : lvalue ASSIGN expr              # assignExpr
    | orExpr                          # topLevel
    ;

orExpr
    : andExpr ( OR andExpr )*         # orChain
    ;

andExpr
    : compExpr ( AND compExpr )*      # andChain
    ;

compExpr
    : addExpr ( compOp addExpr )*     # compareChain   // allows chains like a = b <> c <= d
    ;

addExpr
    : mulExpr ( addOp mulExpr )*      # addSub
    ;

mulExpr
    : unaryExpr ( mulOp unaryExpr )*  # mulDiv
    ;

unaryExpr
    : MINUS unaryExpr                 # negate
    | primaryExpr                     # prim
    ;

primaryExpr
    : constant                        # constLit
    | lvalue                          # lVal
    | LPAREN expr RPAREN              # parenExpr
    | funcCallWithOptPrefix           # funcCallExpr
    ;

// ------------------- L‑values -------------------------------

lvalue
    : ID ( LBRACK expr RBRACK )?       # arrayIndex
    ;

// ------------------- Helpers --------------------------------------

constant
    : INTLIT
    | FLOATLIT
    ;

compOp : EQ | NEQ | LT | GT | LE | GE ;
addOp  : PLUS | MINUS ;
mulOp  : TIMES | DIV ;

// ==================================================================
// LEXER RULES
// ==================================================================

// --- Keywords ---
MAIN      : 'main';
VAR       : 'var';
ARRAY     : 'array';
FUNCTION  : 'function';
LET       : 'let';
IN        : 'in';
BEGIN     : 'begin';
END       : 'end';
IF        : 'if';
THEN      : 'then';
ELSE      : 'else';
WHILE     : 'while';
DO        : 'do';
ENDDO     : 'enddo';
FOR       : 'for';
TO        : 'to';
BREAK     : 'break';
RETURN    : 'return';
ENDIF     : 'endif';
INTEGER   : 'int';
FLOATTY   : 'float';
OF        : 'of';

// --- Symbols & operators ---
ASSIGN    : ':=';
PLUS      : '+';
MINUS     : '-';
TIMES     : '*';
DIV       : '/';
EQ        : '=';
NEQ       : '<>';
LT        : '<';
GT        : '>';
LE        : '<=';
GE        : '>=';
AND       : '&';
OR        : '|';
COMMA     : ',';
COLON     : ':';
SEMICOLON : ';';
LPAREN    : '(';
RPAREN    : ')';
LBRACK    : '[';
RBRACK    : ']';

// --- Identifiers & literals ---
ID        : [A-Za-z] [A-Za-z0-9_]* ;
INTLIT    : [0-9]+ ;
FLOATLIT  : [0-9]+ '.' [0-9]+ ;

// --- Whitespace & comments ---
WS            : [ \t\r\n]+ -> skip ;
COMMENT       : '/*' .*? '*/' -> skip ;
LINE_COMMENT  : '//' ~[\r\n]* -> skip ;
