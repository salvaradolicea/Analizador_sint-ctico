import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileManager {

    // Lee el archivo en UTF-8 y elimina BOM en la primera línea si existe
    public static List<String> leerArchivo(String nombre) throws IOException {
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(nombre), StandardCharsets.UTF_8))) {
            String linea;
            int lineNum = 1;
            while ((linea = br.readLine()) != null) {
                if (lineNum == 1 && linea.startsWith("\uFEFF")) {
                    linea = linea.substring(1);
                }
                linea = linea.replace("\t", " ").trim();
                lineas.add(linea);
                lineNum++;
            }
        }
        return lineas;
    }

    // Escribe líneas en UTF-8
    public static void escribirArchivo(String nombre, List<String> contenido) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(nombre), StandardCharsets.UTF_8))) {
            for (String linea : contenido) {
                bw.write(linea);
                bw.newLine();
            }
        }
    }

    // Escribe una versión depurada en UNA sola línea pero preservando espacios entre líneas
    public static void escribirArchivoDep(String nombre, List<String> contenido) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(nombre), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < contenido.size(); i++) {
                String l = contenido.get(i).trim();
                if (l.isEmpty()) continue;
                if (sb.length() > 0) sb.append(' ');
                sb.append(l);
            }
            bw.write(sb.toString());
        }
    }
}
