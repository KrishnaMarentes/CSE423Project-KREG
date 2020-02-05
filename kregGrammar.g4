grammar kregGrammar;

start :
    asmnt EOF
;

addexpression
    :
    |   INT
    |   ID
    |   addexpression PLUS addexpression
    ;

asmnt
    :
    |  ID ASSIGN INT
    |  ID ASSIGN WORD
    |  ID ASSIGN addexpression
    ;


ASSIGN  :   '=';
PLUS    :   '+';
MINUS   :   '-';
STAR    :   '*';
DIVIDE  :   '/';

SEMICOLN:   ';';


INT     :   [0-9]+;
LETTER  :   [a-zA-Z];
WORD    :   LETTER+;
ID      :   LETTER (LETTER | INT | '_')*;
STRING  :   '"' (WORD | INT)* '"'; //HMMM
