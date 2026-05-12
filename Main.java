import java.util.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {

        try {

            // 1. Leer programa fuente
            List<String> programa = FileManager.leerArchivo("progfte.txt");

            // 2. Ejecutar analizador léxico
            Lexer lexer = new Lexer();
            List<Token> tokens = lexer.analizar(programa);

            // =====================================================
            // 3. Generar archivo progfte.dep
            // =====================================================
            List<String> depurado = new ArrayList<>();
            for (String linea : programa) {
                if (!linea.contains("/*")) {
                    depurado.add(linea.replaceAll("\\s+", ""));
                }
            }
            FileManager.escribirArchivoDep("progfte.dep", depurado);

            // =====================================================
            // 4. Tabla de símbolos
            // =====================================================
            List<EntradaSimbolo> tablaSimbolos = construirTablaSimbolos(tokens);

            // =====================================================
            // 5. Generar progfte.tab
            // =====================================================
            String contenidoTab = generarArchivoTab(tablaSimbolos);
            FileManager.escribirArchivo(
                "progfte.tab",
                Arrays.asList(contenidoTab.split("\n"))
            );

            // =====================================================
            // 6. Generar progfte.tok
            // =====================================================
            String contenidoTok = generarArchivoTok(tokens, lexer.getErrores());
            FileManager.escribirArchivo(
                "progfte.tok",
                Arrays.asList(contenidoTok.split("\n"))
            );

            System.out.println("Análisis léxico completado");

            // ==========================================
            //  ANALIZADOR SINTÁCTICO (AQUÍ VA)
            // ==========================================
            Parser parser = new Parser(tokens);

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
    private static String generarArchivoTok(List<Token> tokens, List<String> errores) {

        StringBuilder sb = new StringBuilder();

        for (Token t : tokens) {
            sb.append(t.toString()).append("\n");
        }

        if (!errores.isEmpty()) {
            sb.append("\nERRORES:\n");
            for (String error : errores) {
                sb.append(error).append("\n");
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
}