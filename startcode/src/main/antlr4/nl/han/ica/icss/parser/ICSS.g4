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
stylerule: (tagselector | idselector | classelector) OPEN_BRACE (variableassignment | ifclause | declaration)* CLOSE_BRACE;
tagselector: LOWER_IDENT;
idselector: ID_IDENT;
classelector: CLASS_IDENT;
declaration: propertyname COLON operation SEMICOLON;
propertyname: LOWER_IDENT;

variableassignment: operation ASSIGNMENT_OPERATOR operation SEMICOLON;

operation:
    operation MUL operation # multiplyoperation |
    operation MIN operation # subtractoperation|
    operation PLUS operation # addoperation|
    CAPITAL_IDENT #variablereference |
    COLOR #colorliteral |
    PIXELSIZE #pixelliteral|
    TRUE #boolliteral|
    FALSE #boolliteral |
    SCALAR #scalarliteral |
    PERCENTAGE #percentageliteral;

ifclause: IF BOX_BRACKET_OPEN operation BOX_BRACKET_CLOSE OPEN_BRACE variableassignment* declaration* ifclause* CLOSE_BRACE elseclause?;
elseclause: ELSE OPEN_BRACE ifclause* variableassignment* declaration* CLOSE_BRACE;
