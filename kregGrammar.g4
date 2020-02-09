grammar kregGrammar;

start :
    program EOF
;

program
    : declarationList
    ;

declarationList
    : declarationList declaration
    | declaration
    ;

declaration
    : varDeclaration
    | funDeclaration
    ;

varDeclaration
    : typeSpecifier varDeclList SEMICOLN
    | scopedVarDeclaration SEMICOLN
    ;

scopedVarDeclaration
    : scopedTypeSpecifier varDeclList
    ;

varDeclList
    : varDeclList ',' varDeclInitialize
    | varDeclInitialize
    ;

varDeclInitialize
    : varDeclId
    | varDeclId '=' expression
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
    : typeSpecifier ID LPAREN params RPAREN ( compoundStmt | SEMICOLN+) //compoundStmt works WAY better here
    ;

params
    : params ',' parameter
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
    | varDeclaration //added here to support edge cases, haven't found any side effects yet
    ;

expressionStmt
    : expression SEMICOLN
    | SEMICOLN
    ;

compoundStmt
    : '{' localDeclarations statementList '}'
    ;

localDeclarations
    : localDeclarations varDeclaration
    |
    ;

statementList
    : statementList statement
    |
    ;

elsifList
    : elsifList 'else if' LPAREN expression RPAREN statement
    |
    ;

selectionStmt
    : 'if' LPAREN expression RPAREN statement elsifList
    | 'if' LPAREN expression RPAREN statement elsifList 'else' statement
    ;

iterationStmt
    : 'while' LPAREN expression RPAREN statement
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
    | mutable '[' expression ']'
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
    : argList ',' expression
    | expression
    ;

constant
    : INT
    | CHARCONST
    | STRINGCONST
    ;

SEMICOLN
    :   ';'
    ;

COLN
    : ':'
    ;

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;

ID
    :   ('_' | LETTER)+ (LETTER | DIGIT | '_')*
    ;

CHARCONST
    : APOS ALLCHARS+ APOS
    //| QUOTE LETTER QUOTE //This is valid in C but I'm going to let STRINGCONST handle it
    ;

STRINGCONST
  : QUOTE ALLCHARS* QUOTE
  ;

APOS
    : '\''
    ;

QUOTE
    : '"'
    ;

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