package sigb.servicio;

import java.time.Clock;
import java.time.LocalDate;
import sigb.dao.EjemplarDAO;
import sigb.dao.MultaDAO;
import sigb.dao.PrestamoDAO;
import sigb.dao.Transaccion;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;
import sigb.modelo.Multa;
import sigb.modelo.Prestamo;

/** RF-02 y RF-05. */
public class DevolucionService {

    private final EjemplarDAO ejemplarDAO;
    private final PrestamoDAO prestamoDAO;
    private final MultaDAO multaDAO;
    private final Transaccion transaccion;
    private final Clock reloj;
    private final double tarifaPorDia;

    public DevolucionService(EjemplarDAO ejemplarDAO, PrestamoDAO prestamoDAO, MultaDAO multaDAO,
                             Transaccion transaccion, Clock reloj, double tarifaPorDia) {
        this.ejemplarDAO = ejemplarDAO;
        this.prestamoDAO = prestamoDAO;
        this.multaDAO = multaDAO;
        this.transaccion = transaccion;
        this.reloj = reloj;
        this.tarifaPorDia = tarifaPorDia;
    }

    public ResultadoDevolucion registrarDevolucion(String codigoEjemplar) {
        if (codigoEjemplar == null || codigoEjemplar.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe ingresar el código del ejemplar.");
        }
        final String codigo = codigoEjemplar.trim();

        return transaccion.ejecutar(() -> {
            Ejemplar ejemplar = ejemplarDAO.buscarPorCodigo(codigo, true)
                    .orElseThrow(() -> new ReglaNegocioException("No existe el ejemplar " + codigo + "."));

            Prestamo prestamo = prestamoDAO.buscarActivoPorEjemplar(ejemplar.getId())
                    .orElseThrow(() -> new ReglaNegocioException(
                            "El ejemplar " + codigo + " no tiene un préstamo activo."));

            LocalDate hoy = LocalDate.now(reloj);
            prestamo.registrarDevolucion(hoy);
            prestamoDAO.registrarDevolucion(prestamo);

            // RF-02: el ejemplar vuelve a estar disponible.
            ejemplar.actualizarEstado(EstadoEjemplar.DISPONIBLE);
            ejemplarDAO.actualizarEstado(ejemplar.getId(), ejemplar.getEstado());

            // RF-05: multa si se superó la fecha límite.
            long diasRetraso = prestamo.diasDeRetraso(hoy);
            Multa multa = null;
            if (diasRetraso > 0) {
                multa = Multa.generar(prestamo.getId(), diasRetraso, tarifaPorDia);
                multaDAO.guardar(multa);
            }
            return new ResultadoDevolucion(prestamo, multa, diasRetraso);
        });
    }
}
