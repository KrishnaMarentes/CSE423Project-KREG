grammar kregGrammar;

start :
    asmnt EOF
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
    :
    |   declarationList
    ;

declarationList
    :

    |   declarationList declaration
    |   declaration
    ;

declaration
    :
    |   varDeclaration
    |   funDeclaration
    ;

varDeclaration
    :
    |   typeSpecifier varDeclList
    ;

scopedVarDeclaration
:
    |   scopedTypeSpecifier varDeclList
    ;

varDeclList
:
    |   varDeclList , varDeclInitialize
    |   varDeclInitialize
    ;

varDeclInitialize
    :
    |   varDeclId OR varDeclId : simpleExpression
    ;

varDeclId
    :
    ID
    ;

scopedTypeSpecifier -> static typeSpecifier OR typeSpecifier
typeSpecifier -> int OR bool OR char

funDeclaration -> typeSpecifier ID ( params ) statement
params -> params , parameter OR parameter
parameter -> typeSpecifier paramId
paramId -> ID

statement -> expressionStmt OR compoundStmt OR selectionStmt OR iterationStmt OR returnStmt OR breakStmt OR gotoStmt
expressionStmt -> expression ; OR ;
compoundStmt -> { localDeclarations statementList }
localDeclarations -> localDeclarations scopedVarDeclaration OR EPS
statementList -> statementList statement OR EPS
elsifList -> elsifList elsif simpleExpression then statement OR EPS
selectionStmt -> if simpleExpression then statement elsifList OR if simpleExpression then statement elsifList else statement
iterationStmt -> while simpleExpression do statement
returnStmt -> return ; OR return expression ;
breakStmt -> break ;
gotoStmt -> goto labelId ;
label -> labelId :
labelId -> ID

expression -> mutable = expression OR mutable += expression OR mutable -= expression OR mutable *= expression OR mutable /= expression OR mutable ++ OR mutable -- OR simpleExpression
simpleExpression -> simpleExpression | andExpression OR andExpression
andExpression -> andExpression & unaryRelExpression OR unaryRelExpression
unaryRelExpression -> ! unaryRelExpression OR relExpression
relExpression -> sumExpression relop sumExpression OR sumExpression
relop -> <= OR < OR > OR >= OR == OR !=
sumExpression -> sumExpression sumop mulExpression OR mulExpression
sumop
    : '+' | '-'
    ;
mulExpression -> mulExpression mulop unaryExpression OR unaryExpression
mulop
    : '*' | '/' | '%'
    ;

unaryExpression -> unaryop unaryExpression OR factor

unaryop
    :   '-' | '*' | '!' | '&'
    ;

factor -> immutable OR mutable
mutable -> ID OR mutable [ expression ]
immutable -> ( expression ) OR call OR constant
call -> ID ( args )
args -> argList OR EPS
argList -> argList , expression OR expression
constant -> NUMCONST OR CHARCONST OR STRINGCONST OR true OR false


