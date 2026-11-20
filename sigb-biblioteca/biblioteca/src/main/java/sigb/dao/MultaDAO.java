package sigb.dao;

import java.util.List;
import sigb.modelo.Multa;

public interface MultaDAO {

    /** Inserta la multa y asigna el id generado. */
    void guardar(Multa multa);

    /** RN-03: ¿el usuario tiene multas sin pagar? */
    boolean tieneMultasPendientes(int idUsuario);

    List<Multa> listarPendientesPorUsuario(int idUsuario);

    /** @return true si la multa existía y estaba sin pagar */
    boolean marcarPagada(int idMulta);
}
