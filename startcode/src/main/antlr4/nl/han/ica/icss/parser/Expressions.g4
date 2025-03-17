grammar Expressions;

expressions: expression+;

expression:
    multi | sum;

//parser-regels
multi: (NUMBER MULTI);
sum: (NUMBER | multi) PLUS (NUMBER | MULTI) (PLUS( NUMBER | multi))*;

//lexer-regels
NUMBER: [0-9]+;
MULTI: '*';
PLUS: '+';


WS: [ \t\r\n]+ -> skip;

