package sigb.servicio;

import sigb.modelo.Multa;
import sigb.modelo.Prestamo;

/**
 * Resultado de registrar una devolución.
 *
 * @param multa         multa generada, o null si se devolvió a tiempo
 * @param diasRetraso   días de retraso (0 si fue a tiempo)
 */
public record ResultadoDevolucion(Prestamo prestamo, Multa multa, long diasRetraso) {

    public boolean generoMulta() {
        return multa != null;
    }
}
