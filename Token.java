//aqui se representa cada unidad léxica reconocida
public class Token {

    private TokenType tipo;
    private String lexema;
    private int linea;

    public Token(TokenType tipo, String lexema, int linea) {
        this.tipo = tipo;//categoria del token, se define en tokentype
        this.lexema = lexema;//texto original del token
        this.linea = linea;//número de línea donde aparece
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getLinea() {
        return linea;
    }
    //devuelve el token en formato tipolexema
    @Override
    public String toString() {
        return "<" + tipo + ", " + lexema + ">";
    }
}