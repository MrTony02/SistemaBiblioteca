package sigb.dao.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import sigb.config.Configuracion;

/**
 * Entrega conexiones JDBC. Dentro de una transacción (ver {@link JdbcTransaccion}) todos los DAO
 * del mismo hilo comparten la misma conexión, y su close() no la cierra hasta que la transacción termina.
 */
public final class ConexionBD {

    private static final ThreadLocal<Connection> EN_TRANSACCION = new ThreadLocal<>();

    private ConexionBD() {
    }

    private static Connection nueva() throws SQLException {
        return DriverManager.getConnection(
                Configuracion.get("db.url", ""),
                Configuracion.get("db.user", ""),
                Configuracion.get("db.password", ""));
    }

    /** Conexión para un DAO. Se usa con try-with-resources. */
    static Connection abrir() throws SQLException {
        Connection real = EN_TRANSACCION.get();
        if (real == null) {
            return nueva();
        }
        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class<?>[] {Connection.class},
                (proxy, metodo, args) -> {
                    if (metodo.getName().equals("close")) {
                        return null; // la cierra JdbcTransaccion al terminar
                    }
                    try {
                        return metodo.invoke(real, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }

    static boolean hayTransaccion() {
        return EN_TRANSACCION.get() != null;
    }

    static Connection iniciarTransaccion() throws SQLException {
        Connection c = nueva();
        c.setAutoCommit(false);
        EN_TRANSACCION.set(c);
        return c;
    }

    static void terminarTransaccion() {
        Connection c = EN_TRANSACCION.get();
        EN_TRANSACCION.remove();
        if (c != null) {
            try {
                c.close();
            } catch (SQLException ignorada) {
                // nada más que hacer
            }
        }
    }

    /** Verifica al arrancar que la base de datos responde. */
    public static void probarConexion() throws SQLException {
        try (Connection c = nueva()) {
            c.isValid(3);
        }
    }
}
