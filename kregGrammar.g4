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
    | SEMICOLN
    ;

varDeclaration
    : typeSpecifier varDeclList SEMICOLN
    | scopedVarDeclaration SEMICOLN
    ;

scopedVarDeclaration
    : scopedTypeSpecifier varDeclList
    ;

varDeclList
    : varDeclList COMMA varDeclInitialize
    | varDeclInitialize
    ;

varDeclInitialize
    : '*'* varDeclId
    | '*'* varDeclId (LSQUARE RSQUARE)* '=' (expression | LCURLY expressionList RCURLY)
    ;

varDeclId
    : ID
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
    : ID
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

elsifList
    : elsifList 'else if' LPAREN expression RPAREN statement
    |
    ;

selectionStmt
    : 'if' LPAREN expression RPAREN statement elsifList
    | 'if' LPAREN expression RPAREN statement elsifList 'else' statement
    | 'switch' LPAREN expression RPAREN (LCURLY switchList ('default' COLN statementList)? RCURLY | (case | 'default' COLN statementList))
    ;

switchList
    : switchList case
    |
    ;

case : 'case' constant COLN statementList;

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
    | mutable '<<' expression
    | mutable '<<=' expression
    | mutable '>>' expression
    | mutable '>>=' expression
    | mutable '&=' expression
    | mutable '|=' expression
    | mutable '^=' expression
    | (mutable | immutable) '&' expression
    | (mutable | immutable) '|' expression
    | (mutable | immutable) '^' expression
    //| mutable '++' //trying this out
    //| mutable '--'
    | simpleExpression
    ;

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
CHARCONST : APOS ALLCHARS+ APOS ;
STRINGCONST : QUOTE ALLCHARS* QUOTE ;


fragment ALLCHARS
    :  (~["\\\r\n] | '\\' (. | EOF))
    ;

fragment LETTER
    :  [a-zA-Z]
    ;

INT
    : DIGIT+
    | ('0x'|'0X')HEXDIGIT+
    | '0'OCTALDIGIT+
    | '0b'BINARYDIGIT+
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