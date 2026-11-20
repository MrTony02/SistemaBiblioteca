package sigb.dao;

import java.util.Optional;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;

public interface EjemplarDAO {

    /**
     * @param bloquear si es true se bloquea la fila hasta terminar la transacción, para que dos
     *                 bibliotecarios no presten el mismo ejemplar a la vez
     */
    Optional<Ejemplar> buscarPorCodigo(String codigo, boolean bloquear);

    void actualizarEstado(int idEjemplar, EstadoEjemplar nuevoEstado);
}
