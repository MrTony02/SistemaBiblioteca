package sigb.dao;

/** Error al acceder a la base de datos (envuelve SQLException). */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
