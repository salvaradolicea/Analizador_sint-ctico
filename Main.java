import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        try {
            // 1. Leer programa fuente (UTF-8 safe)
            List<String> programa = FileManager.leerArchivo("progfte.txt");

            // 2. Preprocesado: concatenar y eliminar comentarios de bloque y de línea
            StringBuilder sbAll = new StringBuilder();
            for (String l : programa) sbAll.append(l).append("\n");
            String todo = sbAll.toString();

            // Eliminar comentarios de bloque /* ... */
            todo = todo.replaceAll("(?s)/\\*.*?\\*/", "");

            // Eliminar comentarios de línea // hasta fin de línea
            todo = todo.replaceAll("//.*(?=\\n)", "");

            // Dividir en líneas limpias
            String[] lineasLimpiasArr = todo.split("\\r?\\n");
            List<String> lineasLimpias = new ArrayList<>();
            for (String l : lineasLimpiasArr) {
                String t = l.replace("\t", " ").trim();
                if (!t.isEmpty()) lineasLimpias.add(t);
            }

            // 3. Ejecutar analizador léxico sobre líneas limpias
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.analizar(lineasLimpias);

            // 4. Generar archivo progfte.dep (una línea, con espacios entre líneas originales)
            StringBuilder depSb = new StringBuilder();
            for (int i = 0; i < lineasLimpias.size(); i++) {
                if (i > 0) depSb.append(' ');
                depSb.append(lineasLimpias.get(i));
            }
            FileManager.escribirArchivoDep("progfte.dep", Arrays.asList(depSb.toString()));

            // 5. Construir tabla de símbolos a partir de tokens (fase DECL)
            List<EntradaSimbolo> tablaSimbolos = construirTablaSimbolos(tokens);
            SymbolTable symTable = new SymbolTable();
            for (EntradaSimbolo e : tablaSimbolos) symTable.agregar(e.getNombre());

            // 6. Generar progfte.tab
            String contenidoTab = generarArchivoTab(tablaSimbolos);
            FileManager.escribirArchivo("progfte.tab", Arrays.asList(contenidoTab.split("\n")));

            // 7. Generar progfte.tok (tokens + errores léxicos)
            String contenidoTok = generarArchivoTok(tokens, lexer.getErrores());
            FileManager.escribirArchivo("progfte.tok", Arrays.asList(contenidoTok.split("\n")));

            System.out.println("Análisis léxico completado");

            // 8. Prechecks sobre tokens antes del parser
            prechecksTokens(tokens);

            // 9. Analizador sintáctico con validación semántica
            Parser parser = new Parser(tokens, symTable);
            try {
                Nodo arbol = parser.parse();
                System.out.println("Análisis sintáctico correcto");
                imprimirArbol(arbol, 0);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // TABLA DE SÍMBOLOS
    // =========================================================
    private static List<EntradaSimbolo> construirTablaSimbolos(List<Token> tokens) {

        List<EntradaSimbolo> tabla = new ArrayList<>();
        boolean dentroDecl = false;
        String tipoActual = "";
        int contador = 1;

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            if (t.getTipo() == TokenType.DECL) {
                dentroDecl = true;
                continue;
            }

            if (t.getTipo() == TokenType.INICIO) {
                break;
            }

            if (dentroDecl && t.getTipo() == TokenType.TIPO) {
                tipoActual = t.getLexema();
                i++;

                while (i < tokens.size() && tokens.get(i).getTipo() != TokenType.PC) {
                    Token actual = tokens.get(i);

                    if (actual.getTipo() == TokenType.ID) {
                        tabla.add(new EntradaSimbolo(
                            contador++,
                            actual.getLexema(),
                            tipoActual,
                            valorInicialPorTipo(tipoActual),
                            300,
                            actual.getLinea()
                        ));
                    }
                    i++;
                }
            }
        }
        return tabla;
    }

    // =========================================================
    private static String generarArchivoTab(List<EntradaSimbolo> tabla) {

        StringBuilder sb = new StringBuilder();

        sb.append(String.format(
            "%-5s | %-20s | %-12s | %-10s | %-6s | %s%n",
            "No.", "VARIABLE", "TIPO", "VALOR INIT", "REF", "LINEA"
        ));
        sb.append("-".repeat(75)).append("\n");

        for (EntradaSimbolo e : tabla) {
            sb.append(String.format(
                "%-5d | %-20s | %-12s | %-10s | %-6d | %d%n",
                e.getNumero(),
                e.getNombre(),
                e.getTipo(),
                e.getValorInicial(),
                e.getReferencia(),
                e.getLinea()
            ));
        }

        return sb.toString();
    }

    // =========================================================
    private static String generarArchivoTok(List<Token> tokens, List<ErrorLexico> errores) {

        StringBuilder sb = new StringBuilder();

        for (Token t : tokens) {
            sb.append(t.toString()).append("\n");
        }

        if (!errores.isEmpty()) {
            sb.append("\nERRORES LEXICOS:\n");
            for (ErrorLexico err : errores) {
                sb.append(err.toString()).append("\n");
            }
        }

        return sb.toString();
    }

    // =========================================================
    private static String valorInicialPorTipo(String tipo) {
        switch (tipo) {
            case "int": return "0";
            case "cad": return "\"\"";
            case "booleano": return "false";
            default: return "indefinido";
        }
    }

    // =========================================================
    public static void imprimirArbol(Nodo nodo, int nivel) {
        for (int i = 0; i < nivel; i++) {
            System.out.print("  ");
        }

        System.out.println(nodo.getValor());

        for (Nodo hijo : nodo.getHijos()) {
            imprimirArbol(hijo, nivel + 1);
        }
    }

    // ---------- Prechecks ----------
    private static void prechecksTokens(List<Token> tokens) {
        checkBalancedParens(tokens);
        checkConsecutiveOperators(tokens);
        checkOperatorAtLineStartOrEnd(tokens);
    }

    private static void checkBalancedParens(List<Token> tokens) {
        int balance = 0;
        for (Token t : tokens) {
            if (t.getTipo() == TokenType.PAREN_OPEN) balance++;
            if (t.getTipo() == TokenType.PAREN_CLOSE) balance--;
            if (balance < 0) {
                throw new RuntimeException("Paréntesis desbalanceados: ')' sin '(' en línea " + t.getLinea());
            }
        }
        if (balance != 0) {
            throw new RuntimeException("Paréntesis desbalanceados: faltan paréntesis de cierre");
        }
    }

    private static void checkConsecutiveOperators(List<Token> tokens) {
        Set<TokenType> operadores = Set.of(TokenType.MAS, TokenType.MENOS, TokenType.MUL, TokenType.DIV, TokenType.ASIG);
        Token prev = null;
        for (Token t : tokens) {
            if (prev != null) {
                if (operadores.contains(prev.getTipo()) && operadores.contains(t.getTipo())) {
                    throw new RuntimeException("Operadores consecutivos detectados en línea " + t.getLinea()
                        + ": '" + prev.getLexema() + "' seguido de '" + t.getLexema() + "'");
                }
                if ((prev.getTipo() == TokenType.CENT || prev.getTipo() == TokenType.ID || prev.getTipo() == TokenType.PAREN_CLOSE)
                    && (t.getTipo() == TokenType.CENT || t.getTipo() == TokenType.ID || t.getTipo() == TokenType.PAREN_OPEN)) {
                    throw new RuntimeException("Falta operador entre operandos en línea " + t.getLinea()
                        + " cerca de '" + prev.getLexema() + " " + t.getLexema() + "'");
                }
            }
            prev = t;
        }
    }

    private static void checkOperatorAtLineStartOrEnd(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if ((t.getTipo() == TokenType.MAS || t.getTipo() == TokenType.MENOS ||
                 t.getTipo() == TokenType.MUL || t.getTipo() == TokenType.DIV || t.getTipo() == TokenType.ASIG)) {
                Token prev = (i > 0) ? tokens.get(i - 1) : null;
                Token next = (i < tokens.size() - 1) ? tokens.get(i + 1) : null;
                if (prev == null || prev.getTipo() == TokenType.PC || prev.getTipo() == TokenType.DECL || prev.getTipo() == TokenType.INICIO) {
                    throw new RuntimeException("Operador '" + t.getLexema() + "' en posición inválida en línea " + t.getLinea());
                }
                if (next == null || next.getTipo() == TokenType.PC || next.getTipo() == TokenType.END) {
                    throw new RuntimeException("Operador '" + t.getLexema() + "' sin operando a la derecha en línea " + t.getLinea());
                }
            }
        }
    }
}
