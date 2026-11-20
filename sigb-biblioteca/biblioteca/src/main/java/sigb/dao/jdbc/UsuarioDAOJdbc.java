package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import sigb.dao.PersistenciaException;
import sigb.dao.UsuarioDAO;
import sigb.modelo.Usuario;

public class UsuarioDAOJdbc implements UsuarioDAO {

    @Override
    public Optional<Usuario> buscarPorDocumento(String documento) {
        String sql = "SELECT id_usuario, nombre, documento FROM usuario WHERE documento = ?";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Usuario(rs.getInt("id_usuario"), rs.getString("nombre"),
                            rs.getString("documento")));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el usuario: " + e.getMessage(), e);
        }
    }
}
