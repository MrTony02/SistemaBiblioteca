package sigb.modelo;

/** Estados posibles de un ejemplar (RN-01). */
public enum EstadoEjemplar {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    EXTRAVIADO("Extraviado");

    private final String etiqueta;

    EstadoEjemplar(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /** Convierte el texto guardado en la base de datos al enum. */
    public static EstadoEjemplar desdeBD(String valor) {
        return valueOf(valor.trim().toUpperCase());
    }
}
