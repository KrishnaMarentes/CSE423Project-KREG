grammar kregGrammar;

start :
    program EOF
;

/*
primaryExpression
    :   Identifier
    |   Constant
    |   StringLiteral+
    |   '(' expression ')'
    ;

postfixExpression
    :   primaryExpression
    |   primaryExpression '[' expression ']'
    |   primaryExpression '(' argumentExpressionList? ')'
    |   primaryExpression '++'
    |   primaryExpression '--'
    ;

argumentExpressionList
    :   assignmentExpression
    |   argumentExpressionList ',' assignmentExpression
    ;

unaryExpression
    :
    |   '++' unaryExpression
    |   '--' unaryExpression
    ;

unaryOperator
    :   '&' | '*' | '+' | '-' | '~' | '!'
    ;

castExpression
    :
    |   unaryExpression
    |   DigitSequence // for
    ;

multiplicativeExpression
    :   castExpression
    |   multiplicativeExpression '*' castExpression
    |   multiplicativeExpression '/' castExpression
    |   multiplicativeExpression '%' castExpression
    ;

additiveExpression
    :   multiplicativeExpression
    |   additiveExpression '+' multiplicativeExpression
    |   additiveExpression '-' multiplicativeExpression
    ;

shiftExpression
    :   additiveExpression
    |   shiftExpression '<<' additiveExpression
    |   shiftExpression '>>' additiveExpression
    ;

relationalExpression
    :   shiftExpression
    |   relationalExpression '<' shiftExpression
    |   relationalExpression '>' shiftExpression
    |   relationalExpression '<=' shiftExpression
    |   relationalExpression '>=' shiftExpression
    ;

equalityExpression
    :   relationalExpression
    |   equalityExpression '==' relationalExpression
    |   equalityExpression '!=' relationalExpression
    ;

assignmentOperator
    :   '=' | '*=' | '/=' | '%=' | '+=' | '-=' | '<<=' | '>>=' | '&=' | '^=' | '|='
    ;

PLUS    :   '+';
MINUS   :   '-';
STAR    :   '*';
DIVIDE  :   '/';

SEMICOLN:   ';';

ID      :   LETTER (LETTER | INT | '_')*;
fragment WORD    :   LETTER+;
fragment LETTER  :   [a-zA-Z];
STRING  :   '"' (WORD | INT)* '"'; //HMMM

// break already a parser rule?
*/

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
    : varDeclId
    | varDeclId '=' simpleExpression
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
    : typeSpecifier ID '(' params ')' statement
    ;

params
    : params ',' parameter
    | parameter
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
    : '(' expression ')'
    | call
    | constant
    ;

call
    : ID '(' args ')'
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
    : NUMCONST
    | CHARCONST
    | STRINGCONST
    ;

SEMICOLN
    :   ';'
    ;

ID
    :   LETTER (LETTER | INT | '_')*
    ;

fragment LETTER
    :   [a-zA-Z]
    ;

fragment INT
    : [0-9]+
    ;
