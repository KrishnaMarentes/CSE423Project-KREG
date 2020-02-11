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
    : 'struct' ID LCURLY unInitVarDeclList RCURLY ID? SEMICOLN
    ;

enumDeclaration
    : 'enum' ID LCURLY enumDeclList RCURLY ID? SEMICOLN
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

structInit
    : 'struct' ID varDeclId SEMICOLN
    | 'struct' ID varDeclId ASSIGNMENT expression SEMICOLN
    ;

enumInit
    : 'enum' ID ID SEMICOLN
    | 'enum' ID ID ASSIGNMENT expression SEMICOLN
    ;

unInitVarDeclList
    : unInitVarDeclList unInitVar
    |
    ;

unInitVar
    : typeSpecifier varDeclId SEMICOLN
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

varDeclList
    : varDeclList COMMA varDeclInitialize
    | varDeclInitialize
    ;

varDeclInitialize
    :  varDeclId
    |  varDeclId '=' (expression | LCURLY expressionList RCURLY)
    ;

varDeclId
    : '*'* ID (LSQUARE RSQUARE)*
    ; 

scopedTypeSpecifier
    : 'static' typeSpecifier
    | typeSpecifier
    ;

typeSpecifier
    : 'int' | 'float' | 'double' | 'char' | 'long' | 'unsigned' | 'signed' | 'void' | 'short'
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
    : '*'* ID (LSQUARE RSQUARE)*
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
    : elsifList 'else if' LPAREN expression RPAREN statement
    |
    ;

selectionStmt
    : 'if' LPAREN expression RPAREN statement elsifList
    | 'if' LPAREN expression RPAREN statement elsifList 'else' statement
    | 'switch' LPAREN expression RPAREN (LCURLY switchList ('default' COLN defaultList)? RCURLY | (case | 'default' COLN defaultList))
    ;

switchList
    : switchList case (defaultList | 'default' COLN defaultList)
    | case
    |
    ;

case : 'case' (INT | CHARCONST) COLN (defaultList | statementList);

iterationStmt
    : 'while' LPAREN expression RPAREN statement
    | 'do' statement 'while' LPAREN expression RPAREN SEMICOLN
    | 'for' LPAREN expressionList SEMICOLN expressionList SEMICOLN expressionList RPAREN statement
    ;

returnStmt
    : 'return' SEMICOLN
    | 'return' expression SEMICOLN
    ;

breakStmt
    : 'break' SEMICOLN
    ;

gotoStmt
    : 'goto' labelId SEMICOLN
    ;

labelStmt
    : labelId COLN
    ;

labelId
    : ID
    ;

expression
    : mutable '=' expression
    | mutable '+=' expression
    | mutable '-=' expression
    | mutable '*=' expression
    | mutable '/=' expression
    | mutable '%=' expression
    | mutable '<<=' expression
    | mutable '>>=' expression
    | mutable '&=' expression
    | mutable '|=' expression
    | mutable '^=' expression
    | (mutable | immutable) '<<' expression
    | (mutable | immutable) '>>' expression
    | (mutable | immutable) '&' expression
    | (mutable | immutable) '|' expression
    | (mutable | immutable) '^' expression
    | simpleExpression
    ;

enumExpression
    : properUnaryOps INT '<<' enumExpression
    | properUnaryOps INT '>>' enumExpression
    | properUnaryOps INT '&' enumExpression
    | properUnaryOps INT '|' enumExpression
    | properUnaryOps INT '^' enumExpression
    | properUnaryOps INT '||' enumExpression
    | properUnaryOps INT '&&' enumExpression
    | properUnaryOps INT '<' enumExpression
    | properUnaryOps INT '<=' enumExpression
    | properUnaryOps INT '>' enumExpression
    | properUnaryOps INT '>=' enumExpression
    | properUnaryOps INT '+' enumExpression
    | properUnaryOps INT '-' enumExpression
    | properUnaryOps INT '*' enumExpression
    | properUnaryOps INT
    ;

properUnaryOps : ('-' | '~' | '!')* ;

simpleExpression
    : simpleExpression '||' andExpression
    | andExpression
    ;

andExpression
    : andExpression '&&' unaryRelExpression
    | unaryRelExpression
    ;

unaryRelExpression
    : '!' unaryRelExpression
    | relExpression
    ;

relExpression
    : sumExpression relop sumExpression
    | relExpression relop relExpression
    | sumExpression
    ;

relop
    : '<=' | '<' | '>' | '>=' | '==' | '!='
    ;

sumExpression
    : sumExpression sumop mulExpression
    | mulExpression
    ;

sumop
    : '+' | '-'
    ;

mulExpression
    : mulExpression mulop unaryExpression
    | unaryExpression
    ;

mulop
    : '*' | '/' | '%'
    ;

unaryExpression
    : unaryop unaryExpression
    | mutable '++'
    | mutable '--'
    | '--' mutable
    | '++' mutable
    | factor
    ;

unaryop
    :   '-' | '*' | '!' | '&' | '~'
    ;

factor
    : immutable
    | mutable
    ;

mutable
    : ID
    | mutable LSQUARE expression RSQUARE
    | mutable ('.' | '->') mutable
    ;

immutable
    : LPAREN expression RPAREN
    | call
    | constant
    ;

call
    : ID LPAREN args RPAREN
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
RETURN : 'return';
WHILE : 'while';
DO : 'do';
SWITCH : 'switch';
CASE : 'case';
DEFAULT: 'default';
STRUCT: 'struct';
ENUM : 'enum';

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

fragment CHARCHARS : (~['\\\r\n] | '\\' (. | EOF)) ;

fragment STRINGCHARS
    :  (~["\\\r\n] | '\\' (. | EOF))
    ;

fragment LETTER
    :  [a-zA-Z]
    ;

INT
    : DIGIT+
    | ('0x'|'0X')HEXDIGIT+
    | '0'OCTALDIGIT+
    | ('0b'|'0B')BINARYDIGIT+
    | FLOAT
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