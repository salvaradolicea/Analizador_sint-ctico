// Coordina todo el proceso en consola
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
            // 3. Generar archivo progfte.dep (código depurado)
            // =====================================================
            List<String> depurado = new ArrayList<>();
            for (String linea : programa) {
                if (!linea.contains("/*")) {
                    depurado.add(linea.replaceAll("\\s+", ""));
                }
            }
            FileManager.escribirArchivoDep("progfte.dep", depurado);

            // =====================================================
            // 4. Construir tabla de símbolos FORMAL
            // =====================================================
            List<EntradaSimbolo> tablaSimbolos = construirTablaSimbolos(tokens);

            // =====================================================
            // 5. Generar archivo progfte.tab
            // =====================================================
            String contenidoTab = generarArchivoTab(tablaSimbolos);
            FileManager.escribirArchivo(
                "progfte.tab",
                Arrays.asList(contenidoTab.split("\n"))
            );

            // =====================================================
            // 6. Generar archivo progfte.tok (FORMATO PROFESIONAL)
            // =====================================================
            String contenidoTok = generarArchivoTok(tokens, lexer.getErrores());
            FileManager.escribirArchivo(
                "progfte.tok",
                Arrays.asList(contenidoTok.split("\n"))
            );

            System.out.println("Análisis léxico completado correctamente.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // CONSTRUCCIÓN DE LA TABLA DE SÍMBOLOS
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
    // GENERAR ARCHIVO progfte.tab
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
    // GENERAR ARCHIVO progfte.tok (TABULAR + REFERENCIAS)
    // =========================================================
    private static String generarArchivoTok(List<Token> tokens, List<String> errores) {

    StringBuilder sb = new StringBuilder();

    String separador = "=".repeat(65) + "\n";
    String divisor   = "-".repeat(65) + "\n";

    sb.append(separador);
    sb.append("  ANALIZADOR LÉXICO — LENGUAJE PF2025\n");
    sb.append("  Archivo: progfte.tok\n");
    sb.append(separador);

    sb.append(String.format(
        " %-5s | %-20s | %-20s | %-10s | %s%n",
        "NUM", "TOKEN", "LEXEMA", "REFERENCIA", "LINEA"
    ));
    sb.append(divisor);

    int contador = 1;
    for (Token t : tokens) {
        sb.append(String.format(
            " %-5d | %-20s | %-20s | %-10d | %d%n",
            contador++,
            t.getTipo(),
            t.getLexema(),
            referenciaToken(t.getTipo()),
            t.getLinea()
        ));
    }

    // ===================== ERRORES =====================
    sb.append("\n");
    sb.append(separador);
    sb.append("  ERRORES LÉXICOS\n");
    sb.append(separador);

    if (errores.isEmpty()) {
        sb.append("  Sin errores léxicos detectados.\n");
    } else {
        for (String error : errores) {
            sb.append("  ").append(error).append("\n");
        }
    }

    sb.append(separador);
    return sb.toString();
}

    // =========================================================
    // REFERENCIAS NUMÉRICAS DE TOKENS
    // =========================================================
    private static int referenciaToken(TokenType tipo) {
        switch (tipo) {
            case PROG:     return 100;
            case DECL:     return 101;
            case TIPO:     return 102;
            case INICIO:   return 103;
            case END:      return 104;
            case ID:       return 300;
            case CENT:     return 400;
            case ASIG:     return 90;
            case MAS:      return 10;
            case MENOS:    return 11;
            case MUL:      return 12;
            case DIV:      return 13;
            case PAREN:    return 75;
            case TESIS:    return 76;
            case COMA:     return 91;
            case PC:       return 92;
            case IMPDIG:   return 110;
            case IMPCAD:   return 111;
            case LEERDIG:  return 112;
            case EOF:      return 999;
            default:       return -1;
        }
    }

    // =========================================================
    // VALOR INICIAL POR TIPO
    // =========================================================
    private static String valorInicialPorTipo(String tipo) {
        switch (tipo) {
            case "int":       return "0";
            case "cad":       return "\"\"";
            case "booleano":  return "false";
            default:          return "indefinido";
        }
    }
}