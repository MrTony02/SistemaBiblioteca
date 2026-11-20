package sigb.dao;

import java.util.Optional;
import sigb.modelo.Prestamo;

public interface PrestamoDAO {

    /** Inserta el préstamo y asigna el id generado. */
    void guardar(Prestamo prestamo);

    /** Préstamo sin devolver de un ejemplar, si existe. */
    Optional<Prestamo> buscarActivoPorEjemplar(int idEjemplar);

    /** Guarda la fecha de devolución del préstamo. */
    void registrarDevolucion(Prestamo prestamo);
}
