public class EntradaSimbolo {

    private int numero;          // No.
    private String nombre;       // VARIABLE
    private String tipo;         // TIPO
    private String valorInicial; // VALOR INIT
    private int referencia;      // REF (300)
    private int linea;           // LINEA

    public EntradaSimbolo(int numero, String nombre, String tipo,
                          String valorInicial, int referencia, int linea) {
        this.numero = numero;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valorInicial = valorInicial;
        this.referencia = referencia;
        this.linea = linea;
    }

    // Getters
    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getValorInicial() {
        return valorInicial;
    }

    public int getReferencia() {
        return referencia;
    }

    public int getLinea() {
        return linea;
    }
}
