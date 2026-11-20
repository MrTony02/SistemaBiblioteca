package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;
import sigb.dao.PersistenciaException;
import sigb.dao.PrestamoDAO;
import sigb.modelo.Prestamo;

public class PrestamoDAOJdbc implements PrestamoDAO {

    @Override
    public void guardar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamo (id_usuario, id_ejemplar, fecha_inicio, fecha_limite) VALUES (?, ?, ?, ?)";
        try (Connection c = ConexionBD.abrir();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getIdUsuario());
            ps.setInt(2, prestamo.getIdEjemplar());
            ps.setDate(3, Date.valueOf(prestamo.getFechaInicio()));
            ps.setDate(4, Date.valueOf(prestamo.getFechaLimite()));
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    prestamo.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al guardar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Prestamo> buscarActivoPorEjemplar(int idEjemplar) {
        String sql = "SELECT id_prestamo, id_usuario, id_ejemplar, fecha_inicio, fecha_limite, fecha_devolucion "
                + "FROM prestamo WHERE id_ejemplar = ? AND fecha_devolucion IS NULL "
                + "ORDER BY id_prestamo DESC LIMIT 1";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idEjemplar);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date devolucion = rs.getDate("fecha_devolucion");
                    LocalDate fechaDevolucion = (devolucion == null) ? null : devolucion.toLocalDate();
                    return Optional.of(new Prestamo(rs.getInt("id_prestamo"), rs.getInt("id_usuario"),
                            rs.getInt("id_ejemplar"), rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_limite").toLocalDate(), fechaDevolucion));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al buscar el préstamo: " + e.getMessage(), e);
        }
    }

    @Override
    public void registrarDevolucion(Prestamo prestamo) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? WHERE id_prestamo = ?";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(prestamo.getFechaDevolucion()));
            ps.setInt(2, prestamo.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al registrar la devolución: " + e.getMessage(), e);
        }
    }
}
