package sigb.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Título del catálogo (por ejemplo, un libro). Tiene uno o varios ejemplares. */
public class Material extends Entidad {

    private final String titulo;
    private final String autor;
    private final List<Ejemplar> ejemplares = new ArrayList<>();

    public Material(int id, String titulo, String autor) {
        super(id);
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void agregarEjemplar(Ejemplar ejemplar) {
        ejemplares.add(ejemplar);
    }

    public List<Ejemplar> getEjemplares() {
        return Collections.unmodifiableList(ejemplares);
    }

    public int contarDisponibles() {
        int total = 0;
        for (Ejemplar e : ejemplares) {
            if (e.estaDisponible()) {
                total++;
            }
        }
        return total;
    }

    /** RF-03: el material está disponible si al menos un ejemplar lo está. */
    public boolean consultarDisponibilidad() {
        return contarDisponibles() > 0;
    }
}
