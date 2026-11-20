package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import sigb.dao.MultaDAO;
import sigb.dao.PersistenciaException;
import sigb.modelo.Multa;

public class MultaDAOJdbc implements MultaDAO {

    @Override
    public void guardar(Multa multa) {
        String sql = "INSERT INTO multa (id_prestamo, valor, pagada) VALUES (?, ?, ?)";
        try (Connection c = ConexionBD.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, multa.getIdPrestamo());
            ps.setDouble(2, multa.getValor());
            ps.setBoolean(3, multa.estaPagada());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    multa.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar la multa: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean tieneMultasPendientes(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM multa m JOIN prestamo p ON p.id_prestamo = m.id_prestamo "
                + "WHERE p.id_usuario = ? AND m.pagada = FALSE";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar las multas: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Multa> listarPendientesPorUsuario(int idUsuario) {
        String sql = "SELECT m.id_multa, m.id_prestamo, m.valor, m.pagada FROM multa m "
                + "JOIN prestamo p ON p.id_prestamo = m.id_prestamo "
                + "WHERE p.id_usuario = ? AND m.pagada = FALSE ORDER BY m.id_multa";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            List<Multa> multas = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    multas.add(new Multa(rs.getInt("id_multa"), rs.getInt("id_prestamo"),
                            rs.getDouble("valor"), rs.getBoolean("pagada")));
                }
            }
            return multas;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al listar las multas: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean marcarPagada(int idMulta) {
        String sql = "UPDATE multa SET pagada = TRUE WHERE id_multa = ? AND pagada = FALSE";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idMulta);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar el pago: " + e.getMessage(), e);
        }
    }
}
