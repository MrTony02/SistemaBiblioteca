package sigb.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Préstamo de un ejemplar a un usuario. */
public class Prestamo extends Entidad {

    private final int idUsuario;
    private final int idEjemplar;
    private final LocalDate fechaInicio;
    private final LocalDate fechaLimite;
    private LocalDate fechaDevolucion;

    public Prestamo(int id, int idUsuario, int idEjemplar,
                    LocalDate fechaInicio, LocalDate fechaLimite, LocalDate fechaDevolucion) {
        super(id);
        // RN-02: un préstamo válido siempre tiene fecha límite.
        if (fechaInicio == null || fechaLimite == null) {
            throw new IllegalArgumentException("RN-02: el préstamo debe tener fecha de inicio y fecha límite.");
        }
        if (fechaLimite.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha límite no puede ser anterior a la fecha de inicio.");
        }
        this.idUsuario = idUsuario;
        this.idEjemplar = idEjemplar;
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
        this.fechaDevolucion = fechaDevolucion;
    }

    /** RF-04: crea un préstamo nuevo con la fecha límite asignada automáticamente. */
    public static Prestamo registrarPrestamo(int idUsuario, int idEjemplar, LocalDate hoy, int diasPrestamo) {
        return new Prestamo(0, idUsuario, idEjemplar, hoy, hoy.plusDays(diasPrestamo), null);
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdEjemplar() {
        return idEjemplar;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public boolean estaActivo() {
        return fechaDevolucion == null;
    }

    public void registrarDevolucion(LocalDate fecha) {
        if (!estaActivo()) {
            throw new IllegalStateException("El préstamo ya fue devuelto.");
        }
        if (fecha.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La devolución no puede ser anterior al inicio del préstamo.");
        }
        this.fechaDevolucion = fecha;
    }

    /**
     * Días de retraso respecto a la fecha límite. Si el préstamo ya se devolvió se usa la
     * fecha de devolución; si sigue activo, la fecha de referencia indicada.
     */
    public long diasDeRetraso(LocalDate fechaReferencia) {
        LocalDate fin = (fechaDevolucion != null) ? fechaDevolucion : fechaReferencia;
        return Math.max(0, ChronoUnit.DAYS.between(fechaLimite, fin));
    }
}
