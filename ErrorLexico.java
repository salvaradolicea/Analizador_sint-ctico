public class ErrorLexico {

    private String mensaje;
    private int linea;
    private int columna; // aproximada, opcional

    public ErrorLexico(String mensaje, int linea) {
        this(mensaje, linea, -1);
    }

    public ErrorLexico(String mensaje, int linea, int columna) {
        this.mensaje = mensaje;
        this.linea = linea;
        this.columna = columna;
    }

    public void mostrarError() {
        if (columna > 0) {
            System.out.println("Error léxico en línea " + linea + ", col " + columna + ": " + mensaje);
        } else {
            System.out.println("Error léxico en línea " + linea + ": " + mensaje);
        }
    }

    @Override
    public String toString() {
        if (columna > 0) return "Linea " + linea + ", col " + columna + ": " + mensaje;
        return "Linea " + linea + ": " + mensaje;
    }
}
