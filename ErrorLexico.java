public class ErrorLexico {

    private String mensaje;//descripcion de error
    private int linea;//número de linea donde ocurre el error
    //Este metodo imprime el error en consola 
    public ErrorLexico(String mensaje, int linea) {
        this.mensaje = mensaje;
        this.linea = linea;
    }

    public void mostrarError() {
        System.out.println("Error léxico en línea " + linea + ": " + mensaje);
    }
}