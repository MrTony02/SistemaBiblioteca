package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import sigb.dao.PersistenciaException;
import sigb.dao.Transaccion;

public class JdbcTransaccion implements Transaccion {

    @Override
    public <T> T ejecutar(Supplier<T> trabajo) {
        if (ConexionBD.hayTransaccion()) {
            return trabajo.get(); // ya estamos dentro de una: se une a ella
        }
        Connection conexion = null;
        try {
            conexion = ConexionBD.iniciarTransaccion();
            T resultado = trabajo.get();
            conexion.commit();
            return resultado;
        } catch (RuntimeException e) {
            revertir(conexion);
            throw e;
        } catch (SQLException e) {
            revertir(conexion);
            throw new PersistenciaException("No se pudo completar la transacción: " + e.getMessage(), e);
        } finally {
            ConexionBD.terminarTransaccion();
        }
    }

    private static void revertir(Connection conexion) {
        if (conexion != null) {
            try {
                conexion.rollback();
            } catch (SQLException ignorada) {
                // la conexión se cierra enseguida; el servidor descarta lo pendiente
            }
        }
    }
}
