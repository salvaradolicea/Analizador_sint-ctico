public class EntradaSimbolo {

    private int numero;
    private String nombre;
    private String tipo;
    private String valorInicial;
    private int referencia;
    private int linea;

    public EntradaSimbolo(int numero, String nombre, String tipo,
                          String valorInicial, int referencia, int linea) {
        this.numero = numero;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorInicial = valorInicial;
        this.referencia = referencia;
        this.linea = linea;
    }

    public int getNumero() { return numero; }
    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public String getValorInicial() { return valorInicial; }
    public int getReferencia() { return referencia; }
    public int getLinea() { return linea; }
}
