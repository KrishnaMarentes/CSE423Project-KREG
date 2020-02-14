grammar kregGrammar;

program
    : declarationList EOF
    ;

declarationList
    : declarationList declaration
    | declaration
    ;

declaration
    : varDeclaration
    | funDeclaration
    | structDeclaration
    | enumDeclaration
    | SEMICOLN
    ;

structDeclaration
    : STATIC? STRUCT ID LCURLY unInitVarDecl* RCURLY ID? SEMICOLN
    ;

structInit
    : STATIC? STRUCT ID STAR* varDeclList SEMICOLN
    | STATIC? STRUCT ID STAR* varDeclId ASSIGNMENT (structExpressionList | LCURLY structExpressionList RCURLY) SEMICOLN
    ;

enumDeclaration
    : ENUM ID LCURLY enumDeclList RCURLY ID? SEMICOLN
    ;

enumDeclList
    : enumDeclList COMMA enumId
    | enumId
    |
    ;

enumId
    : ID ASSIGNMENT enumExpression
    | ID
    ;

enumInit
    : ENUM ID ID SEMICOLN
    | ENUM ID ID ASSIGNMENT expression SEMICOLN
    ;

unInitVarDecl
    : typeSpecifier unInitVarDeclList SEMICOLN
    ;

unInitVarDeclList
    : unInitVarDeclList COMMA varDeclId
    | varDeclId
    |
    ;

varDeclaration
    : typeSpecifier varDeclList SEMICOLN
    | scopedVarDeclaration SEMICOLN
    | structInit
    | enumInit
    ;

scopedVarDeclaration
    : scopedTypeSpecifier varDeclList
    ;

forLoopVars
    : typeSpecifier varDeclList
    | expressionList
    ;

varDeclList
    : varDeclList COMMA varDeclInitialize
    | varDeclInitialize
    ;

varDeclInitialize
    :  varDeclId
    |  varDeclId ASSIGNMENT (expression | LCURLY expressionList RCURLY)
    ;

varDeclId
    : ID (LSQUARE expression? RSQUARE)*
    ; 

scopedTypeSpecifier
    : STATIC typeSpecifier
    | typeSpecifier
    ;

typeSpecifier
    : (TYPE_INT | TYPE_FLOAT | TYPE_DOUBLE | TYPE_CHAR | TYPE_LONG | TYPE_UNSIGNED | TYPE_SIGNED | TYPE_VOID | TYPE_SHORT) STAR*
    ;

funDeclaration
    : typeSpecifier ID LPAREN params RPAREN (compoundStmt | SEMICOLN)
    ;

params
    : params COMMA parameter
    | parameter
    |
    ;

parameter
    : typeSpecifier paramId
    ;

paramId
    : ID (LSQUARE RSQUARE)*
    ;

statement
    : expressionStmt
    | compoundStmt
    | selectionStmt
    | iterationStmt
    | returnStmt
    | breakStmt
    | gotoStmt
    | labelStmt
    ;

structExpressionList
    : structExpressionList COMMA PERIOD? expression
    | PERIOD? expression
    |
    ;

expressionList
    : expressionList COMMA expression
    | expression
    |
    ;

expressionStmt
    : expression SEMICOLN
    | SEMICOLN
    ;

compoundStmt
    : LCURLY statementList RCURLY
    ;

statementList
    : statementList ( statement | varDeclaration )
    |
    ;

defaultList
    : statementList ( statement | varDeclaration )
    | statement
    ;

elsifList
    : elsifList ELSE IF LPAREN expression RPAREN statement
    |
    ;

selectionStmt
    : IF LPAREN expression RPAREN statement elsifList
    | IF LPAREN expression RPAREN statement elsifList ELSE statement
    | SWITCH LPAREN expression RPAREN switchCase defaultList
    | SWITCH LPAREN expression RPAREN DEFAULT COLN defaultList
    | SWITCH LPAREN expression RPAREN LCURLY switchList (DEFAULT COLN defaultList)? RCURLY
    ;

switchList
    : switchList switchCase
    | switchCase
    |
    ;

switchCase : CASE (INT | CHARCONST) COLN (defaultList | statementList);

iterationStmt
    : WHILE LPAREN expression RPAREN statement
    | DO statement WHILE LPAREN expression RPAREN SEMICOLN
    | FOR LPAREN forLoopVars SEMICOLN expressionList SEMICOLN expressionList RPAREN statement
    ;

returnStmt
    : RETURN SEMICOLN
    | RETURN expression SEMICOLN
    ;

breakStmt
    : BREAK SEMICOLN
    ;

gotoStmt
    : GOTO labelId SEMICOLN
    ;

labelStmt
    : labelId COLN
    ;

labelId
    : ID
    ;

expression
    : mutable ASSIGNMENT expression
    | mutable ADD_ASSIGNMENT expression
    | mutable SUB_ASSIGNMENT expression
    | mutable STAR_ASSIGNMENT expression
    | mutable DIV_ASSIGNMENT expression
    | mutable MOD_ASSIGNMENT expression
    | mutable LSHIFT_ASSIGNMENT expression
    | mutable RSHIFT_ASSIGNMENT expression
    | mutable AND_ASSIGNMENT expression
    | mutable OR_ASSIGNMENT expression
    | mutable XOR_ASSIGNMENT expression
    | (mutable | immutable) LSHIFT expression
    | (mutable | immutable) RSHIFT expression
    | (mutable | immutable) AND expression
    | (mutable | immutable) OR expression
    | (mutable | immutable) CARET expression
    | simpleExpression
    ;

enumExpression
    : properUnaryOps INT LSHIFT enumExpression
    | properUnaryOps INT RSHIFT enumExpression
    | properUnaryOps INT AND enumExpression
    | properUnaryOps INT OR enumExpression
    | properUnaryOps INT CARET enumExpression
    | properUnaryOps INT OROR enumExpression
    | properUnaryOps INT ANDAND enumExpression
    | properUnaryOps INT COMPARE_LESSTHAN enumExpression
    | properUnaryOps INT COMPARE_LESSTHANEQUALS enumExpression
    | properUnaryOps INT COMPARE_GREATERTHAN enumExpression
    | properUnaryOps INT COMPARE_GREATERTHANEQUALS enumExpression
    | properUnaryOps INT PLUS enumExpression
    | properUnaryOps INT MINUS enumExpression
    | properUnaryOps INT STAR enumExpression
    | properUnaryOps INT
    ;

properUnaryOps : (MINUS | TILDE | NOT)* ;

simpleExpression
    : simpleExpression OROR andExpression
    | andExpression
    ;

andExpression
    : andExpression ANDAND unaryRelExpression
    | unaryRelExpression
    ;

unaryRelExpression
    : NOT unaryRelExpression
    | relExpression
    ;

relExpression
    : sumExpression relop sumExpression
    | relExpression relop relExpression
    | sumExpression
    ;

relop
    : COMPARE_LESSTHANEQUALS
    | COMPARE_LESSTHAN
    | COMPARE_GREATERTHAN
    | COMPARE_GREATERTHANEQUALS
    | COMPARE_EQUALS
    | COMPARE_NOTEQUALS
    ;

sumExpression
    : sumExpression sumop mulExpression
    | mulExpression
    ;

sumop
    : PLUS | MINUS
    ;

mulExpression
    : mulExpression mulop unaryExpression
    | unaryExpression
    ;

mulop
    : STAR | DIV | MOD
    ;

unaryExpression
    : unaryop unaryExpression
    | mutable PLUSPLUS
    | mutable MINUSMINUS
    | MINUSMINUS mutable
    | PLUSPLUS mutable
    | factor
    ;

unaryop
    :   MINUS | STAR | NOT | AND | TILDE
    ;

factor
    : immutable
    | mutable
    ;

mutable
    : STAR* ID
    | mutable LSQUARE expression RSQUARE
    | mutable (PERIOD | ARROW) mutable
    | immutable (PERIOD | ARROW) mutable
    | LPAREN expression RPAREN(PERIOD | ARROW) mutable
    | STAR* LPAREN expression RPAREN
    ;

immutable
    : LPAREN expression RPAREN
    | call
    | constant
    ;

call
    : ID LPAREN args RPAREN
    | SIZEOF LPAREN (STRUCT ID STAR* | typeSpecifier | ID) RPAREN
    ;

args
    : argList
    |
    ;

argList
    : argList COMMA expression
    | expression
    ;

constant
    : INT
    | CHARCONST
    | STRINGCONST
    ;

STATIC : 'static';
BREAK : 'break';
GOTO : 'goto';
CONTINUE : 'continue';
FOR : 'for';
IF : 'if';
ELSE: 'else';
RETURN : 'return';
WHILE : 'while';
DO : 'do';
SWITCH : 'switch';
CASE : 'case';
DEFAULT: 'default';
STRUCT: 'struct';
ENUM : 'enum';
SIZEOF : 'sizeof';

TYPE_INT : 'int';
TYPE_FLOAT : 'float';
TYPE_DOUBLE : 'double';
TYPE_CHAR : 'char';
TYPE_LONG : 'long';
TYPE_UNSIGNED : 'unsigned';
TYPE_SIGNED : 'signed';
TYPE_VOID : 'void';
TYPE_SHORT : 'short';

COMPARE_LESSTHAN : '<';
COMPARE_LESSTHANEQUALS : '<=';
COMPARE_GREATERTHAN : '>';
COMPARE_GREATERTHANEQUALS : '>=';
COMPARE_EQUALS : '==' ;
COMPARE_NOTEQUALS : '!=' ;

ANDAND : '&&';
OROR : '||';
OR : '|';
AND : '&';
CARET : '^';
NOT : '!';
TILDE : '~';
LSHIFT : '<<' ;
RSHIFT : '>>' ;

PLUS : '+';
PLUSPLUS : '++';
MINUS : '-';
MINUSMINUS : '--';
STAR : '*';
DIV : '/';
MOD : '%';

ASSIGNMENT : '=';
STAR_ASSIGNMENT : '*=';
DIV_ASSIGNMENT : '/=';
MOD_ASSIGNMENT : '%=';
ADD_ASSIGNMENT : '+=';
SUB_ASSIGNMENT : '-=';
AND_ASSIGNMENT : '&=';
XOR_ASSIGNMENT : '^=';
OR_ASSIGNMENT  : '|=';
LSHIFT_ASSIGNMENT : '<<=';
RSHIFT_ASSIGNMENT : '>>=';

SEMICOLN: ';' ;
COLN : ':' ;
PERIOD : '.';
ARROW : '->';
COMMA : ',' ;
LPAREN : '(' ;
RPAREN : ')' ;
LCURLY : '{' ;
RCURLY : '}' ;
LSQUARE : '[' ;
RSQUARE : ']' ;
APOS : '\'' ;
QUOTE : '"' ;

ID : ('_' | LETTER)+ (LETTER | DIGIT | '_')* ;
CHARCONST : APOS CHARCHARS+ APOS ;
//CHARCONST : APOS CHARCHARS APOS ; //can also do it like this, which will generate a lexer error if 'ab' is entered
//but char tmp = 'ab'; in gcc is a compiler time -warning-
STRINGCONST : QUOTE STRINGCHARS* QUOTE ;

INT
    : DIGIT+
    | ('0x'|'0X')HEXDIGIT+
    | '0'OCTALDIGIT+
    | ('0b'|'0B')BINARYDIGIT+
    | FLOAT
    ;

fragment CHARCHARS : (~['\\\r\n] | '\\' (. | EOF)) ;

fragment STRINGCHARS
    :  (~["\\\r\n] | '\\' (. | EOF))
    ;

fragment LETTER
    :  [a-zA-Z]
    ;

fragment DIGIT
    : [0-9]
    ;

fragment HEXDIGIT
    : [0-9A-Fa-f]
    ;

fragment OCTALDIGIT
    : [0-7]
    ;

fragment BINARYDIGIT
    : [0-1]
    ;

fragment FLOAT
    :   [0-9]+ '.' [0-9]+ EXP?('f'|'F')?
    |   '.' [0-9]+ EXP?('f'|'F')?
    |   [0-9]+ EXP('f'|'F')?
    |   [0-9]+ ('f'|'F')?
    ;

fragment EXP : ('e'|'E') ('+'|'-')? [0-9]+ ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;

Unknown  : . ;
