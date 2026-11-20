package sigb.modelo;

/** Estudiante (usuario) de la biblioteca. */
public class Usuario extends Entidad {

    private String nombre;
    private String documento;

    public Usuario(int id, String nombre, String documento) {
        super(id);
        this.nombre = nombre;
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    @Override
    public String toString() {
        return nombre + " (" + documento + ")";
    }
}
