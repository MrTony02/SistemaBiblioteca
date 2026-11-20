package sigb.config;

import java.time.Clock;
import sigb.dao.EjemplarDAO;
import sigb.dao.MaterialDAO;
import sigb.dao.MultaDAO;
import sigb.dao.PrestamoDAO;
import sigb.dao.Transaccion;
import sigb.dao.UsuarioDAO;
import sigb.dao.jdbc.EjemplarDAOJdbc;
import sigb.dao.jdbc.JdbcTransaccion;
import sigb.dao.jdbc.MaterialDAOJdbc;
import sigb.dao.jdbc.MultaDAOJdbc;
import sigb.dao.jdbc.PrestamoDAOJdbc;
import sigb.dao.jdbc.UsuarioDAOJdbc;
import sigb.servicio.CatalogoService;
import sigb.servicio.DevolucionService;
import sigb.servicio.MultaService;
import sigb.servicio.PrestamoService;

/** Ensambla las capas: DAO JDBC -> servicios. La vista solo recibe este objeto. */
public record Servicios(CatalogoService catalogo, PrestamoService prestamos,
                        DevolucionService devoluciones, MultaService multas) {

    public static Servicios crearConJdbc() {
        UsuarioDAO usuarioDAO = new UsuarioDAOJdbc();
        MaterialDAO materialDAO = new MaterialDAOJdbc();
        EjemplarDAO ejemplarDAO = new EjemplarDAOJdbc();
        PrestamoDAO prestamoDAO = new PrestamoDAOJdbc();
        MultaDAO multaDAO = new MultaDAOJdbc();
        Transaccion transaccion = new JdbcTransaccion();
        Clock reloj = Clock.systemDefaultZone();

        int diasPrestamo = Configuracion.getInt("prestamo.dias", 7);
        double tarifa = Configuracion.getDouble("multa.valor_por_dia", 1000);

        return new Servicios(
                new CatalogoService(materialDAO),
                new PrestamoService(usuarioDAO, ejemplarDAO, prestamoDAO, multaDAO, transaccion, reloj, diasPrestamo),
                new DevolucionService(ejemplarDAO, prestamoDAO, multaDAO, transaccion, reloj, tarifa),
                new MultaService(usuarioDAO, multaDAO));
    }
}
