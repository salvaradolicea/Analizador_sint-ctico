/*
define todas las categorías posibles de tokens
que el analizador léxico puede reconocer
El lexer usar TokenType para clasificar cada lexema
La clase Token guarda un TokenType junto con el lexema y la línea
ErrorLexico se activa cuando el Lexer asigna el tipo ERROR
 */
public enum TokenType {

    // Palabras reservadas
    PROG,       // pf2025
    DECL,       // decl
    INICIO,     // inicio
    END,        // end
    IMPDIG,
    IMPCAD,
    LEERDIG,

    // Tipos
    TIPO,       // int, cad, booleano

    // Operadores
    MAS,        // +
    MENOS,      // -
    MUL,        // *
    DIV,        // /
    ASIG,       // :=
    IGUAL,      // =

    // Signos
    PC,         // ;
    COMA,       // ,
    PAREN,      // (
    TESIS,      // )

    // Otros
    ID,
    CENT,

    EOF,
    ERROR
}