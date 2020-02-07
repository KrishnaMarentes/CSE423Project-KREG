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
    : typeSpecifier varDeclList
    ;

scopedVarDeclaration
    : scopedTypeSpecifier varDeclList
    ;

varDeclList
    : varDeclList ',' varDeclInitialize
    | varDeclInitialize
    ;

varDeclInitialize
    : varDeclId SEMICOLN
    | varDeclId '=' simpleExpression SEMICOLN
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
    : typeSpecifier ID LPAREN params RPAREN statement
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
    ;

expressionStmt
    : expression SEMICOLN
    | SEMICOLN
    ;

compoundStmt
    : '{' localDeclarations statementList '}'
    ;

localDeclarations
    : localDeclarations scopedVarDeclaration
    |
    ;

statementList
    : statementList statement
    |
    ;

elsifList
    : elsifList 'elsif' simpleExpression 'then' statement
    |
    ;

selectionStmt
    : 'if' simpleExpression 'then' statement elsifList
    | 'if' simpleExpression 'then' statement elsifList 'else' statement
    ;

iterationStmt
    : 'while' simpleExpression 'do' statement
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

label
    : labelId SEMICOLN
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
    | mutable '++'
    | mutable '--'
    | simpleExpression
    ;

simpleExpression
    : simpleExpression '|' andExpression
    | andExpression
    ;

andExpression
    : andExpression '&' unaryRelExpression
    | unaryRelExpression
    ;

unaryRelExpression
    : '!' unaryRelExpression
    | relExpression
    ;

relExpression
    : sumExpression relop sumExpression
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
    | factor
    ;

unaryop
    :   '-' | '*' | '!' | '&'
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

LPAREN
    : '('
    ;

RPAREN
    : ')'
    ;

ID
    :   LETTER (LETTER | INT | '_')*
    ;

fragment LETTER
    :  [a-zA-Z]
    ;

fragment INT
    : [0-9]+
    ;

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