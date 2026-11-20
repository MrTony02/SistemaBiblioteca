package sigb.servicio;

/** Se lanzó porque la operación incumple una regla de negocio o un dato es inválido. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
