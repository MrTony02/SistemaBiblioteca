package sigb;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;
import sigb.modelo.Material;
import sigb.modelo.Multa;
import sigb.modelo.Prestamo;
import sigb.modelo.Usuario;
import sigb.servicio.CatalogoService;
import sigb.servicio.DevolucionService;
import sigb.servicio.MultaService;
import sigb.servicio.PrestamoService;
import sigb.servicio.ReglaNegocioException;
import sigb.servicio.ResultadoDevolucion;

/** Pruebas de las reglas de negocio sin JUnit ni base de datos: java -cp out sigb.PruebasReglas */
public class PruebasReglas {

    private static int total = 0;
    private static int fallos = 0;

    static final Clock RELOJ = Clock.fixed(Instant.parse("2026-11-17T15:00:00Z"), ZoneId.of("America/Bogota"));
    static final LocalDate HOY = LocalDate.of(2026, 11, 17);

    public static void main(String[] args) {
        prestamoExitosoAsignaFechaLimite();
        rn01NoPrestaEjemplarPrestado();
        rn01NoPrestaEjemplarExtraviado();
        usuarioOEjemplarInexistente();
        camposVacios();
        rn02PrestamoSinFechaLimiteEsInvalido();
        devolucionATiempoNoGeneraMulta();
        devolucionTardiaGeneraMulta();
        rn03MultaPendienteBloqueaPrestamosHastaPagar();
        devolverEjemplarSinPrestamoActivo();
        catalogoMuestraDisponibilidad();

        System.out.println();
        System.out.println((total - fallos) + " de " + total + " verificaciones correctas.");
        if (fallos > 0) {
            System.exit(1);
        }
    }

    // ---- escenario base -------------------------------------------------------------------

    private static BaseMemoria base() {
        BaseMemoria b = new BaseMemoria();
        b.usuarios.add(new Usuario(1, "Ana Gómez", "1020304050"));
        b.usuarios.add(new Usuario(2, "Carlos Ruiz", "1098765432"));
        Material java = new Material(1, "Introducción a Java", "Schildt");
        Ejemplar e1 = new Ejemplar(11, "EJ-1001", 1, EstadoEjemplar.DISPONIBLE);
        Ejemplar e2 = new Ejemplar(12, "EJ-1002", 1, EstadoEjemplar.PRESTADO);
        Ejemplar e3 = new Ejemplar(13, "EJ-1003", 1, EstadoEjemplar.EXTRAVIADO);
        Ejemplar e4 = new Ejemplar(14, "EJ-1004", 1, EstadoEjemplar.DISPONIBLE);
        for (Ejemplar e : List.of(e1, e2, e3, e4)) {
            java.agregarEjemplar(e);
            b.ejemplares.add(e);
        }
        Material sql = new Material(2, "Bases de Datos SQL", "Elmasri");
        Ejemplar e5 = new Ejemplar(15, "EJ-2001", 2, EstadoEjemplar.PRESTADO);
        sql.agregarEjemplar(e5);
        b.ejemplares.add(e5);
        b.materiales.add(java);
        b.materiales.add(sql);
        return b;
    }

    private static PrestamoService prestamos(BaseMemoria b) {
        return new PrestamoService(b, b, b, b, b, RELOJ, 7);
    }

    private static DevolucionService devoluciones(BaseMemoria b) {
        return new DevolucionService(b, b, b, b, RELOJ, 1000);
    }

    // ---- casos ---------------------------------------------------------------------------

    static void prestamoExitosoAsignaFechaLimite() {
        System.out.println("RF-01 / RF-04: préstamo exitoso");
        BaseMemoria b = base();
        Prestamo p = prestamos(b).registrarPrestamo("1020304050", "EJ-1001");
        check(p.getFechaLimite().equals(HOY.plusDays(7)), "la fecha límite es hoy + 7 días");
        check(p.getFechaInicio().equals(HOY), "la fecha de inicio es hoy");
        check(b.ejemplares.get(0).getEstado() == EstadoEjemplar.PRESTADO, "el ejemplar queda PRESTADO");
        check(b.prestamos.size() == 1, "se guardó un préstamo");
    }

    static void rn01NoPrestaEjemplarPrestado() {
        System.out.println("RN-01: ejemplar prestado");
        BaseMemoria b = base();
        esperaRegla(() -> prestamos(b).registrarPrestamo("1020304050", "EJ-1002"), "no está disponible");
        check(b.prestamos.isEmpty(), "no se creó ningún préstamo");
    }

    static void rn01NoPrestaEjemplarExtraviado() {
        System.out.println("RN-01: ejemplar extraviado");
        BaseMemoria b = base();
        esperaRegla(() -> prestamos(b).registrarPrestamo("1020304050", "EJ-1003"), "Extraviado");
    }

    static void usuarioOEjemplarInexistente() {
        System.out.println("Validaciones de existencia");
        BaseMemoria b = base();
        esperaRegla(() -> prestamos(b).registrarPrestamo("999", "EJ-1001"), "No existe un usuario");
        esperaRegla(() -> prestamos(b).registrarPrestamo("1020304050", "EJ-9999"), "No existe el ejemplar");
    }

    static void camposVacios() {
        System.out.println("Validaciones de campos vacíos");
        BaseMemoria b = base();
        esperaRegla(() -> prestamos(b).registrarPrestamo("  ", "EJ-1001"), "documento");
        esperaRegla(() -> prestamos(b).registrarPrestamo("1020304050", ""), "código");
    }

    static void rn02PrestamoSinFechaLimiteEsInvalido() {
        System.out.println("RN-02: fecha límite obligatoria");
        try {
            new Prestamo(0, 1, 1, HOY, null, null);
            check(false, "debió rechazar un préstamo sin fecha límite");
        } catch (IllegalArgumentException e) {
            check(true, "el modelo rechaza un préstamo sin fecha límite");
        }
    }

    static void devolucionATiempoNoGeneraMulta() {
        System.out.println("RF-02: devolución a tiempo");
        BaseMemoria b = base();
        prestamos(b).registrarPrestamo("1020304050", "EJ-1001");
        ResultadoDevolucion r = devoluciones(b).registrarDevolucion("EJ-1001");
        check(!r.generoMulta(), "no hay multa");
        check(b.ejemplares.get(0).estaDisponible(), "el ejemplar vuelve a DISPONIBLE");
        check(r.prestamo().getFechaDevolucion().equals(HOY), "se guarda la fecha de devolución");
    }

    private static void prestamoVencido(BaseMemoria b) {
        // EJ-1002 prestado a Carlos (id 2), límite hace 5 días
        b.prestamos.add(new Prestamo(50, 2, 12, HOY.minusDays(12), HOY.minusDays(5), null));
    }

    static void devolucionTardiaGeneraMulta() {
        System.out.println("RF-05: devolución tardía");
        BaseMemoria b = base();
        prestamoVencido(b);
        ResultadoDevolucion r = devoluciones(b).registrarDevolucion("EJ-1002");
        check(r.diasRetraso() == 5, "5 días de retraso");
        check(r.generoMulta() && r.multa().getValor() == 5000.0, "multa de 5 x 1000 = 5000");
        check(!r.multa().estaPagada(), "la multa nace sin pagar");
        check(b.multas.size() == 1, "la multa se guardó");
        check(b.ejemplares.get(1).estaDisponible(), "el ejemplar quedó DISPONIBLE");
    }

    static void rn03MultaPendienteBloqueaPrestamosHastaPagar() {
        System.out.println("RN-03: multas pendientes");
        BaseMemoria b = base();
        prestamoVencido(b);
        devoluciones(b).registrarDevolucion("EJ-1002");
        esperaRegla(() -> prestamos(b).registrarPrestamo("1098765432", "EJ-1001"), "multas pendientes");

        MultaService multas = new MultaService(b, b);
        List<Multa> pendientes = multas.listarPendientes("1098765432");
        check(pendientes.size() == 1, "el usuario tiene 1 multa pendiente");
        multas.registrarPago(pendientes.get(0).getId());
        esperaRegla(() -> multas.registrarPago(pendientes.get(0).getId()), "ya estaba pagada");

        Prestamo p = prestamos(b).registrarPrestamo("1098765432", "EJ-1001");
        check(p != null, "tras pagar, puede pedir un nuevo préstamo");
        // otro usuario sin multas no se ve afectado
        check(prestamos(b).registrarPrestamo("1020304050", "EJ-1004") != null, "otro usuario no se ve afectado");
    }

    static void devolverEjemplarSinPrestamoActivo() {
        System.out.println("Devolución sin préstamo activo");
        BaseMemoria b = base();
        esperaRegla(() -> devoluciones(b).registrarDevolucion("EJ-1001"), "no tiene un préstamo activo");
        esperaRegla(() -> devoluciones(b).registrarDevolucion("EJ-0000"), "No existe el ejemplar");
    }

    static void catalogoMuestraDisponibilidad() {
        System.out.println("RF-03: catálogo");
        BaseMemoria b = base();
        CatalogoService catalogo = new CatalogoService(b);
        List<Material> todos = catalogo.buscar("");
        check(todos.size() == 2, "sin filtro devuelve los 2 materiales");
        check(todos.get(0).consultarDisponibilidad() && todos.get(0).contarDisponibles() == 2,
                "Introducción a Java: 2 ejemplares disponibles");
        check(!todos.get(1).consultarDisponibilidad(), "Bases de Datos SQL: ninguno disponible");
        check(catalogo.buscar("sql").size() == 1, "la búsqueda por título ignora mayúsculas");
    }

    // ---- utilidades ------------------------------------------------------------------------

    private static void check(boolean condicion, String descripcion) {
        total++;
        if (!condicion) {
            fallos++;
        }
        System.out.println("  " + (condicion ? "OK    " : "FALLA ") + descripcion);
    }

    private static void esperaRegla(Runnable accion, String fragmentoMensaje) {
        try {
            accion.run();
            check(false, "debió lanzar ReglaNegocioException (" + fragmentoMensaje + ")");
        } catch (ReglaNegocioException e) {
            check(e.getMessage().contains(fragmentoMensaje), "rechaza: " + e.getMessage());
        }
    }
}
