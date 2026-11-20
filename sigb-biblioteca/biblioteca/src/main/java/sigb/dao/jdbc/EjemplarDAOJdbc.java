package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import sigb.dao.EjemplarDAO;
import sigb.dao.PersistenciaException;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;

public class EjemplarDAOJdbc implements EjemplarDAO {

    @Override
    public Optional<Ejemplar> buscarPorCodigo(String codigo, boolean bloquear) {
        String sql = "SELECT id_ejemplar, codigo, id_material, estado FROM ejemplar WHERE codigo = ?"
                + (bloquear ? " FOR UPDATE" : "");
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Ejemplar(rs.getInt("id_ejemplar"), rs.getString("codigo"),
                            rs.getInt("id_material"), EstadoEjemplar.desdeBD(rs.getString("estado"))));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el ejemplar: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarEstado(int idEjemplar, EstadoEjemplar nuevoEstado) {
        String sql = "UPDATE ejemplar SET estado = ? WHERE id_ejemplar = ?";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idEjemplar);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar el ejemplar: " + e.getMessage(), e);
        }
    }
}
