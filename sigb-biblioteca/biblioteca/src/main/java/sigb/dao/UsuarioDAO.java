package sigb.dao;

import java.util.Optional;
import sigb.modelo.Usuario;

public interface UsuarioDAO {

    Optional<Usuario> buscarPorDocumento(String documento);
}
