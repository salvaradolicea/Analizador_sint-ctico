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
        if (index < tokens.size() - 1) {
            index++;
        }
    }

    private void consumir(TokenType tipo, String mensaje) {
        if (actual().getTipo() == tipo) {
            avanzar();
        } else {
            error(mensaje);
        }
    }

    private void error(String mensaje) {
        throw new RuntimeException(
            "Error sintáctico en línea " + actual().getLinea()
            + ": " + mensaje + " -> '" + actual().getLexema() + "'"
        );
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
            case ID:
                return asignacion();
            case IMPDIG:
                return impresion();
            case LEERDIG:
                return lectura();
            default:
                error("Sentencia inválida");
                return null;
        }
    }

    private Nodo asignacion() {
        Nodo nodo = new Nodo("ASIGNACION");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");
        nodo.agregarHijo(new Nodo(id.getLexema()));

        consumir(TokenType.ASIG, "Falta ':='");

        nodo.agregarHijo(expresion());

        //  VALIDACIÓN CLAVE (corrige tu problema principal)
        if (actual().getTipo() == TokenType.ID ||
            actual().getTipo() == TokenType.CENT) {
            error("Expresión mal formada: operandos consecutivos o falta operador");
        }

        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    private Nodo impresion() {
        Nodo nodo = new Nodo("IMPDIG");

        consumir(TokenType.IMPDIG, "Error en impresión");

        consumir(TokenType.PAREN, "Falta '('");

        nodo.agregarHijo(expresion());

        consumir(TokenType.TESIS, "Falta ')'");

        consumir(TokenType.PC, "Falta ';'");

        return nodo;
    }

    private Nodo lectura() {
        Nodo nodo = new Nodo("LEERDIG");

        consumir(TokenType.LEERDIG, "Error en lectura");

        consumir(TokenType.PAREN, "Falta '('");

        Token id = actual();
        consumir(TokenType.ID, "Se esperaba ID");

        nodo.agregarHijo(new Nodo(id.getLexema()));

        consumir(TokenType.TESIS, "Falta ')'");

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

        //  OPERANDOS válidos
        if (t.getTipo() == TokenType.CENT ||
            t.getTipo() == TokenType.ID) {

            avanzar();
            return new Nodo(t.getLexema());
        }

        //  PARÉNTESIS
        if (t.getTipo() == TokenType.PAREN) {
            consumir(TokenType.PAREN, "");

            Nodo nodo = expresion();

            consumir(TokenType.TESIS, "Falta ')'");

            return nodo;
        }

        //  ERROR MEJORADO
        error("Falta operando o expresión inválida");
        return null;
    }
}