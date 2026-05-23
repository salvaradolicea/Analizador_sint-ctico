public class Token {
    private TokenType tipo;
    private String lexema;
    private int linea;

    public Token(TokenType tipo, String lexema, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.linea = linea;
    }

    public TokenType getTipo() { return tipo; }
    public String getLexema() { return lexema; }
    public int getLinea() { return linea; }

    @Override
    public String toString() {
        return "<" + tipo + ", " + lexema + ", ln:" + linea + ">";
    }
}
