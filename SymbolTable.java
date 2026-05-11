//almacena los identificadores encontrados en el codigo fuente
import java.util.HashSet;
import java.util.Set;

public class SymbolTable {
    //este atributo evita los duplicados
    private Set<String> tabla;

    public SymbolTable() {
        tabla = new HashSet<>();
    }
    //añande un identificador
    public void agregar(String id) {
        tabla.add(id);
    }
    //verifica si ya está registrado
    public boolean existe(String id) {
        return tabla.contains(id);
    }
    //devuelve todos los identificadores
    public Set<String> getTabla() {
        return tabla;
    }
}
//esta clase permite saber que variables se han declarado
//y cuales han sido usadas