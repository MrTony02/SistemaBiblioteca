package sigb.modelo;

/** Multa generada por devolver un material después de la fecha límite (RF-05). */
public class Multa extends Entidad {

    private final int idPrestamo;
    private final double valor;
    private boolean pagada;

    public Multa(int id, int idPrestamo, double valor, boolean pagada) {
        super(id);
        this.idPrestamo = idPrestamo;
        this.valor = valor;
        this.pagada = pagada;
    }

    /** Crea una multa nueva (sin pagar) a partir de los días de retraso. */
    public static Multa generar(int idPrestamo, long diasRetraso, double tarifaPorDia) {
        return new Multa(0, idPrestamo, calcularMulta(diasRetraso, tarifaPorDia), false);
    }

    public static double calcularMulta(long diasRetraso, double tarifaPorDia) {
        if (diasRetraso <= 0) {
            return 0;
        }
        return diasRetraso * tarifaPorDia;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public double getValor() {
        return valor;
    }

    public boolean estaPagada() {
        return pagada;
    }

    public void pagar() {
        if (pagada) {
            throw new IllegalStateException("La multa ya está pagada.");
        }
        this.pagada = true;
    }
}
