package sigb.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lee db.properties del classpath y, si existe un db.properties junto al programa
 * (carpeta de trabajo), sus valores tienen prioridad.
 */
public final class Configuracion {

    private static final Properties PROPIEDADES = new Properties();

    static {
        try (InputStream in = Configuracion.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                PROPIEDADES.load(new java.io.InputStreamReader(in, StandardCharsets.UTF_8));
            }
            Path externo = Path.of("db.properties");
            if (Files.isRegularFile(externo)) {
                try (Reader r = Files.newBufferedReader(externo, StandardCharsets.UTF_8)) {
                    PROPIEDADES.load(r);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer db.properties", e);
        }
    }

    private Configuracion() {
    }

    public static String get(String clave, String porDefecto) {
        return PROPIEDADES.getProperty(clave, porDefecto).trim();
    }

    public static int getInt(String clave, int porDefecto) {
        return Integer.parseInt(get(clave, String.valueOf(porDefecto)));
    }

    public static double getDouble(String clave, double porDefecto) {
        return Double.parseDouble(get(clave, String.valueOf(porDefecto)));
    }
}
