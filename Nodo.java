import java.util.*;

public class Nodo {

    String valor;
    List<Nodo> hijos;

    public Nodo(String valor) {
        this.valor = valor;
        this.hijos = new ArrayList<>();
    }

    public void agregarHijo(Nodo hijo) {
        hijos.add(hijo);
    }

    public String getValor() {
        return valor;
    }

    public List<Nodo> getHijos() {
        return hijos;
    }
}