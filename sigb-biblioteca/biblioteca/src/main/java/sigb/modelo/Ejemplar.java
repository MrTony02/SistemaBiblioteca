package sigb.modelo;

/** Copia física de un material. Es lo que realmente se presta. */
public class Ejemplar extends Entidad {

    private final String codigo;
    private final int idMaterial;
    private EstadoEjemplar estado;

    public Ejemplar(int id, String codigo, int idMaterial, EstadoEjemplar estado) {
        super(id);
        this.codigo = codigo;
        this.idMaterial = idMaterial;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public int getIdMaterial() {
        return idMaterial;
    }

    public EstadoEjemplar getEstado() {
        return estado;
    }

    public boolean estaDisponible() {
        return estado == EstadoEjemplar.DISPONIBLE;
    }

    public void actualizarEstado(EstadoEjemplar nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El estado del ejemplar no puede ser nulo.");
        }
        this.estado = nuevoEstado;
    }

    @Override
    public String toString() {
        return codigo + " [" + estado.getEtiqueta() + "]";
    }
}
