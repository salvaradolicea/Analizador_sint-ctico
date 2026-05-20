import java.util.*;

public class Parser {

    private List<Token> tokens;
    private int index = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    private Token actual() {
        return tokens.get(index);
    }

    private void avanzar() {
        if (index < tokens.size() - 1) index++;
    }

    private void consumir(TokenType tipo, String mensaje) {
        if (actual().getTipo() == tipo) {
            avanzar();
        } else {
            throw syntaxErrorFor(actual(), mensaje);
        }
    }

    // Generador uniforme de RuntimeException con token problemático
    private RuntimeException syntaxErrorFor(Token t, String mensaje) {
        return new RuntimeException(
            "Error sintáctico en línea " + t.getLinea() + ": " + mensaje + " -> '" + t.getLexema() + "'"
        );
    }

    // Mensajes especializados para expresiones
    private String msgMissingOperatorBefore(Token t) {
        return "Expresión mal formada: falta operador entre operandos antes de";
    }

    private String msgMissingOperatorAfter(Token t) {
        return "Expresión mal formada: falta operador después de";
    }

    private String msgMissingOperandFor(Token t) {
        return "Expresión mal formada: falta operando después de";
    }

    private String msgUnexpectedToken(Token t) {
        return "Token inesperado en la expresión";
    }

    // =========================
    // PROGRAMA PRINCIPAL
    // =========================
    public Nodo parse() {
        Nodo raiz = new Nodo("PROGRAMA");

        consumir(TokenType.PROG, "Se esperaba 'pf2025'");

        raiz.agregarHijo(declaraciones());
        raiz.agregarHijo(bloque());

        consumir(TokenType.EOF, "Fin de archivo esperado");

        return raiz;
    }

    // =========================
    // DECLARACIONES
    // =========================
    private Nodo declaraciones() {
        Nodo nodo = new Nodo("DECL");

        consumir(TokenType.DECL, "Se esperaba 'decl'");

        while (actual().getTipo() == TokenType.TIPO) {
            nodo.agregarHijo(listaDeclaracion());
        }

        return nodo;
    }

    private Nodo listaDeclaracion() {
        Nodo nodo = new Nodo("VAR_DECL");

        Token tipo = actual();
        consumir(TokenType.TIPO, "Se esperaba tipo");

        nodo.agregarHijo(new Nodo(tipo.getLexema()));

        nodo.agregarHijo(idLista());

        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    private Nodo idLista() {
        Nodo nodo = new Nodo("IDS");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba identificador");

        nodo.agregarHijo(new Nodo(id.getLexema()));

        while (actual().getTipo() == TokenType.COMA) {
            consumir(TokenType.COMA, "Error en lista de IDs");
            Token id2 = actual();
            consumir(TokenType.ID, "Se esperaba ID");
            nodo.agregarHijo(new Nodo(id2.getLexema()));
        }

        return nodo;
    }

    // =========================
    // BLOQUE
    // =========================
    private Nodo bloque() {
        Nodo nodo = new Nodo("BLOQUE");

        consumir(TokenType.INICIO, "Se esperaba 'inicio'");

        while (actual().getTipo() != TokenType.END) {
            nodo.agregarHijo(sentencia());
        }

        consumir(TokenType.END, "Se esperaba 'end'");

        return nodo;
    }

    // =========================
    // SENTENCIAS
    // =========================
    private Nodo sentencia() {
        Token t = actual();

        switch (t.getTipo()) {
            case ID: return asignacion();
            case IMPDIG: return impresion();
            case LEERDIG: return lectura();
            default: throw syntaxErrorFor(t, "Sentencia inválida");
        }
    }

    private Nodo asignacion() {
        Nodo nodo = new Nodo("ASIGNACION");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");
        nodo.agregarHijo(new Nodo(id.getLexema()));

        consumir(TokenType.ASIG, "Falta ':='");

        Nodo expr = expresion();
        nodo.agregarHijo(expr);

        Token siguiente = actual();

        // Si el siguiente token no es ';' ni EOF, es muy probable que la expresión esté mal formada.
        if (siguiente.getTipo() != TokenType.PC && siguiente.getTipo() != TokenType.EOF) {
            // Si el token siguiente es un número o ID, falta operador entre operandos
            if (siguiente.getTipo() == TokenType.CENT || siguiente.getTipo() == TokenType.ID) {
                throw syntaxErrorFor(siguiente, msgMissingOperatorBefore(siguiente));
            }
            // Si el token siguiente es '(', falta operador entre operandos (ej: x ( ... ) )
            if (siguiente.getTipo() == TokenType.PAREN_OPEN) {
                throw syntaxErrorFor(siguiente, msgMissingOperatorBefore(siguiente));
            }
            // Otros casos: token inesperado
            throw syntaxErrorFor(siguiente, msgUnexpectedToken(siguiente));
        }

        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    private Nodo impresion() {
        Nodo nodo = new Nodo("IMPDIG");

        consumir(TokenType.IMPDIG, "Error en impresión");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Nodo expr = expresion();
        nodo.agregarHijo(expr);

        Token siguiente = actual();
        if (siguiente.getTipo() != TokenType.PAREN_CLOSE) {
            if (siguiente.getTipo() == TokenType.CENT || siguiente.getTipo() == TokenType.ID) {
                throw syntaxErrorFor(siguiente, msgMissingOperatorBefore(siguiente));
            }
            throw syntaxErrorFor(siguiente, msgUnexpectedToken(siguiente));
        }

        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    private Nodo lectura() {
        Nodo nodo = new Nodo("LEERDIG");

        consumir(TokenType.LEERDIG, "Error en lectura");
        consumir(TokenType.PAREN_OPEN, "Falta '('");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");
        nodo.agregarHijo(new Nodo(id.getLexema()));

        consumir(TokenType.PAREN_CLOSE, "Falta ')'");
        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    // =========================
    // EXPRESIONES
    // =========================
    private Nodo expresion() {
        Nodo nodo = termino();

        while (actual().getTipo() == TokenType.MAS ||
               actual().getTipo() == TokenType.MENOS) {

            Token op = actual();
            avanzar();

            // Si después del operador no viene un operando válido, error claro
            Token siguiente = actual();
            if (!(siguiente.getTipo() == TokenType.CENT || siguiente.getTipo() == TokenType.ID || siguiente.getTipo() == TokenType.PAREN_OPEN)) {
                throw syntaxErrorFor(siguiente, msgMissingOperandFor(op));
            }

            Nodo derecho = termino();

            Nodo nuevo = new Nodo(op.getLexema());
            nuevo.agregarHijo(nodo);
            nuevo.agregarHijo(derecho);

            nodo = nuevo;
        }

        return nodo;
    }

    private Nodo termino() {
        Nodo nodo = factor();

        while (actual().getTipo() == TokenType.MUL ||
               actual().getTipo() == TokenType.DIV) {

            Token op = actual();
            avanzar();

            Token siguiente = actual();
            if (!(siguiente.getTipo() == TokenType.CENT || siguiente.getTipo() == TokenType.ID || siguiente.getTipo() == TokenType.PAREN_OPEN)) {
                throw syntaxErrorFor(siguiente, msgMissingOperandFor(op));
            }

            Nodo derecho = factor();

            Nodo nuevo = new Nodo(op.getLexema());
            nuevo.agregarHijo(nodo);
            nuevo.agregarHijo(derecho);

            nodo = nuevo;
        }

        return nodo;
    }

    private Nodo factor() {
        Token t = actual();

        if (t.getTipo() == TokenType.CENT || t.getTipo() == TokenType.ID) {
            avanzar();
            return new Nodo(t.getLexema());
        }

        if (t.getTipo() == TokenType.PAREN_OPEN) {
            consumir(TokenType.PAREN_OPEN, "Falta '('");
            Nodo nodo = expresion();

            Token siguiente = actual();
            if (siguiente.getTipo() != TokenType.PAREN_CLOSE) {
                // Si no hay ')' después de la subexpresión, indicar que falta cierre o que hay token inesperado
                if (siguiente.getTipo() == TokenType.CENT || siguiente.getTipo() == TokenType.ID || siguiente.getTipo() == TokenType.PAREN_OPEN) {
                    throw syntaxErrorFor(siguiente, msgMissingOperatorBefore(siguiente));
                }
                throw syntaxErrorFor(siguiente, "Expresión mal formada dentro de paréntesis");
            }

            consumir(TokenType.PAREN_CLOSE, "Falta ')'");
            return nodo;
        }

        // Token inesperado en lugar de operando
        throw syntaxErrorFor(t, "Falta operando o expresión inválida");
    }
}
