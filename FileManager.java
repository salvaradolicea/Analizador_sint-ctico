import java.io.*;
import java.util.*;

public class FileManager {

    // Devuelve una lista con todas las líneas del archivo
    public static List<String> leerArchivo(String nombre) throws IOException {
        List<String> lineas = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(nombre));

        String linea;
        while ((linea = br.readLine()) != null) {
            lineas.add(linea);
        }

        br.close();
        return lineas;
    }

    // Escribe una lista de cadenas en un archivo (cada línea por separado)
    public static void escribirArchivo(String nombre, List<String> contenido) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(nombre));

        for (String linea : contenido) {
            bw.write(linea);
            bw.newLine();
        }

        bw.close();
    }

    // Escribe un archivo depurado en UNA sola línea sin espacios ni saltos
    public static void escribirArchivoDep(String nombre, List<String> contenido) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(nombre));

        StringBuilder sb = new StringBuilder();
        for (String linea : contenido) {
            sb.append(linea.replaceAll("\\s+", "")); // quita espacios internos
        }

        bw.write(sb.toString()); // todo en una sola línea
        bw.close();
    }
}
