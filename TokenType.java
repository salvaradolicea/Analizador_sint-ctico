public enum TokenType {
    // Palabras reservadas
    PROG, DECL, INICIO, END, IMPDIG, IMPCAD, LEERDIG,
    // Tipos
    TIPO,
    // Operadores
    MAS, MENOS, MUL, DIV, ASIG, IGUAL,
    // Signos
    PC, COMA, PAREN_OPEN, PAREN_CLOSE,
    // Otros
    ID, CENT,
    EOF, ERROR
}
