grammar Expressions;

//lexer-regels
Number: [0-9]+;
Multi: '*';
Div: '/';
Plus: '+';
Min: '-';

Operator:[*/+-];

WS: [ \t\r\n]+ -> skip;

//parser-regels


expression:(Number? (Multi|Div|Plus|Min) Number)+;