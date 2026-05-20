import java.util.HashSet;
import java.util.Set;

public class SymbolTable {
    private Set<String> tabla;

    public SymbolTable() {
        tabla = new HashSet<>();
    }

    public void agregar(String id) { tabla.add(id); }
    public boolean existe(String id) { return tabla.contains(id); }
    public Set<String> getTabla() { return tabla; }
}
