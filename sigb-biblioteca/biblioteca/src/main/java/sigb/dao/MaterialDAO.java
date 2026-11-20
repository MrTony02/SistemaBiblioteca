package sigb.dao;

import java.util.List;
import sigb.modelo.Material;

public interface MaterialDAO {

    /** Materiales cuyo título contiene el texto (vacío = todos), ya con sus ejemplares cargados. */
    List<Material> buscarPorTitulo(String texto);
}
