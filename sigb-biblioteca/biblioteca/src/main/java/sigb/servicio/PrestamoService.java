package sigb.servicio;

import java.time.Clock;
import java.time.LocalDate;
import sigb.dao.EjemplarDAO;
import sigb.dao.MultaDAO;
import sigb.dao.PrestamoDAO;
import sigb.dao.Transaccion;
import sigb.dao.UsuarioDAO;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;
import sigb.modelo.Prestamo;
import sigb.modelo.Usuario;

/** RF-01, RF-04, RN-01, RN-02, RN-03. */
public class PrestamoService {

    private final UsuarioDAO usuarioDAO;
    private final EjemplarDAO ejemplarDAO;
    private final PrestamoDAO prestamoDAO;
    private final MultaDAO multaDAO;
    private final Transaccion transaccion;
    private final Clock reloj;
    private final int diasPrestamo;

    public PrestamoService(UsuarioDAO usuarioDAO, EjemplarDAO ejemplarDAO, PrestamoDAO prestamoDAO,
                           MultaDAO multaDAO, Transaccion transaccion, Clock reloj, int diasPrestamo) {
        this.usuarioDAO = usuarioDAO;
        this.ejemplarDAO = ejemplarDAO;
        this.prestamoDAO = prestamoDAO;
        this.multaDAO = multaDAO;
        this.transaccion = transaccion;
        this.reloj = reloj;
        this.diasPrestamo = diasPrestamo;
    }

    public Prestamo registrarPrestamo(String documento, String codigoEjemplar) {
        final String doc = requerido(documento, "el documento del estudiante");
        final String codigo = requerido(codigoEjemplar, "el código del ejemplar");

        return transaccion.ejecutar(() -> {
            Usuario usuario = usuarioDAO.buscarPorDocumento(doc)
                    .orElseThrow(() -> new ReglaNegocioException("No existe un usuario con documento " + doc + "."));

            Ejemplar ejemplar = ejemplarDAO.buscarPorCodigo(codigo, true)
                    .orElseThrow(() -> new ReglaNegocioException("No existe el ejemplar " + codigo + "."));

            // RN-01: no se presta un ejemplar "Prestado" o "Extraviado".
            if (!ejemplar.estaDisponible()) {
                throw new ReglaNegocioException("El ejemplar " + codigo + " no está disponible (estado: "
                        + ejemplar.getEstado().getEtiqueta() + ").");
            }

            // RN-03: sin multas activas sin pagar.
            if (multaDAO.tieneMultasPendientes(usuario.getId())) {
                throw new ReglaNegocioException(usuario.getNombre()
                        + " tiene multas pendientes de pago y no puede solicitar nuevos préstamos.");
            }

            // RF-04 / RN-02: la fecha límite se asigna automáticamente.
            Prestamo prestamo = Prestamo.registrarPrestamo(usuario.getId(), ejemplar.getId(),
                    LocalDate.now(reloj), diasPrestamo);
            prestamoDAO.guardar(prestamo);

            ejemplar.actualizarEstado(EstadoEjemplar.PRESTADO);
            ejemplarDAO.actualizarEstado(ejemplar.getId(), ejemplar.getEstado());
            return prestamo;
        });
    }

    private static String requerido(String valor, String descripcion) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ReglaNegocioException("Debe ingresar " + descripcion + ".");
        }
        return valor.trim();
    }
}
