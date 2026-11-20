package sigb.servicio;

import java.util.List;
import sigb.dao.MaterialDAO;
import sigb.modelo.Material;

/** RF-03: consulta del catálogo y su disponibilidad. */
public class CatalogoService {

    private final MaterialDAO materialDAO;

    public CatalogoService(MaterialDAO materialDAO) {
        this.materialDAO = materialDAO;
    }

    /** @param titulo texto a buscar en el título; vacío devuelve todo el catálogo */
    public List<Material> buscar(String titulo) {
        return materialDAO.buscarPorTitulo(titulo == null ? "" : titulo.trim());
    }
}
