grammar ICSS;

//--- LEXER: ---

// IF support:
IF: 'if';
ELSE: 'else';
BOX_BRACKET_OPEN: '[';
BOX_BRACKET_CLOSE: ']';


//Literals
TRUE: 'TRUE';
FALSE: 'FALSE';
PIXELSIZE: [0-9]+ 'px';
PERCENTAGE: [0-9]+ '%';
SCALAR: [0-9]+;


//Color value takes precedence over id idents
COLOR: '#' [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f] [0-9a-f];

//Specific identifiers for id's and css classes
ID_IDENT: '#' [a-z0-9\-]+;
CLASS_IDENT: '.' [a-z0-9\-]+;

//General identifiers
LOWER_IDENT: [a-z] [a-z0-9\-]*;
CAPITAL_IDENT: [A-Z] [A-Za-z0-9_]*;

//All whitespace is skipped
WS: [ \t\r\n]+ -> skip;

//
OPEN_BRACE: '{';
CLOSE_BRACE: '}';
SEMICOLON: ';';
COLON: ':';
PLUS: '+';
MIN: '-';
MUL: '*';
ASSIGNMENT_OPERATOR: ':=';




//--- PARSER: ---
stylesheet: variableassignment* stylerule*;
stylerule: (tagselector | idselector | classelector) OPEN_BRACE (ifclause | declaration)* CLOSE_BRACE;
tagselector: LOWER_IDENT;
idselector: ID_IDENT;
classelector: CLASS_IDENT;
declaration: propertyname COLON (colorliteral | pixelliteral | variablereference | addoperation) SEMICOLON;
propertyname: LOWER_IDENT;
colorliteral: COLOR;
pixelliteral: PIXELSIZE;
boolliteral: TRUE | FALSE;
scalarliteral: SCALAR;

variableassignment: variablereference ASSIGNMENT_OPERATOR (colorliteral | pixelliteral | boolliteral) SEMICOLON;
variablereference: CAPITAL_IDENT;

addoperation: (variablereference | scalarliteral | multiplyoperation | pixelliteral) PLUS (variablereference | scalarliteral | multiplyoperation | pixelliteral);
multiplyoperation: (scalarliteral | pixelliteral) MUL (scalarliteral | pixelliteral);

ifclause: IF BOX_BRACKET_OPEN (variablereference) BOX_BRACKET_CLOSE OPEN_BRACE declaration* ifclause* CLOSE_BRACE elseclause?;
elseclause: ELSE OPEN_BRACE declaration CLOSE_BRACE;