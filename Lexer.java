import java.util.*;
import java.util.regex.*;

public class Lexer {

    private List<ErrorLexico> errores = new ArrayList<>();

    private static final Set<String> tipos = Set.of("int", "cad", "booleano");

    // Analiza una lista de líneas ya preprocesadas (sin comentarios de bloque ni //)
    public List<Token> analizar(List<String> lineas) {
        List<Token> tokens = new ArrayList<>();
        int numLinea = 1;

        for (String lineaOriginal : lineas) {
            String linea = lineaOriginal;

            // Procesar operadores multi-caracter primero
            linea = linea.replace(":=", " := ");

            // Insertar espacios alrededor de separadores y operadores simples
            linea = linea
                    .replace(";", " ; ")
                    .replace(",", " , ")
                    .replace("(", " ( ")
                    .replace(")", " ) ")
                    .replace("+", " + ")
                    .replace("-", " - ")
                    .replace("*", " * ")
                    .replace("/", " / ");

            linea = linea.trim().replaceAll("\\s+", " ");

            if (linea.isEmpty()) {
                numLinea++;
                continue;
            }

            String[] palabras = linea.split(" ");

            int colApprox = 1;
            for (String palabra : palabras) {
                if (palabra.isEmpty()) { colApprox += 1; continue; }

                Token token = reconocerToken(palabra, numLinea);

                if (token.getTipo() == TokenType.ERROR) {
                    errores.add(new ErrorLexico("Símbolo no identificado '" + palabra + "'", numLinea, colApprox));
                    // continuar sin detener el análisis
                } else {
                    tokens.add(token);
                }

                colApprox += palabra.length() + 1;
            }

            numLinea++;
        }

        tokens.add(new Token(TokenType.EOF, "EOF", Math.max(1, numLinea - 1)));
        return tokens;
    }

    private Token reconocerToken(String lexema, int linea) {
        switch (lexema) {
            case "pf2025": return new Token(TokenType.PROG, lexema, linea);
            case "decl": return new Token(TokenType.DECL, lexema, linea);
            case "inicio": return new Token(TokenType.INICIO, lexema, linea);
            case "end": return new Token(TokenType.END, lexema, linea);
            case "impdig": return new Token(TokenType.IMPDIG, lexema, linea);
            case "impcad": return new Token(TokenType.IMPCAD, lexema, linea);
            case "leerdig": return new Token(TokenType.LEERDIG, lexema, linea);

            case "+": return new Token(TokenType.MAS, lexema, linea);
            case "-": return new Token(TokenType.MENOS, lexema, linea);
            case "*": return new Token(TokenType.MUL, lexema, linea);
            case "/": return new Token(TokenType.DIV, lexema, linea);
            case ":=": return new Token(TokenType.ASIG, lexema, linea);
            case ";": return new Token(TokenType.PC, lexema, linea);
            case ",": return new Token(TokenType.COMA, lexema, linea);
            case "(": return new Token(TokenType.PAREN_OPEN, lexema, linea);
            case ")": return new Token(TokenType.PAREN_CLOSE, lexema, linea);
        }

        if (tipos.contains(lexema))
            return new Token(TokenType.TIPO, lexema, linea);

        if (lexema.matches("[0-9]+"))
            return new Token(TokenType.CENT, lexema, linea);

        if (lexema.matches("[a-zA-Z][a-zA-Z0-9]*"))
            return new Token(TokenType.ID, lexema, linea);

        return new Token(TokenType.ERROR, lexema, linea);
    }

    public List<ErrorLexico> getErrores() {
        return errores;
    }
}
