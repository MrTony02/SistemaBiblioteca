package sigb.modelo;

/**
 * Clase base abstracta de las entidades persistentes (herencia y abstracción).
 * Un id igual a 0 indica que la entidad aún no se ha guardado.
 */
public abstract class Entidad {

    private int id;

    protected Entidad(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean esNueva() {
        return id == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entidad otra = (Entidad) o;
        return id != 0 && id == otra.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
