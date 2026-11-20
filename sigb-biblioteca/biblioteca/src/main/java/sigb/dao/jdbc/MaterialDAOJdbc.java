package sigb.dao.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import sigb.dao.MaterialDAO;
import sigb.dao.PersistenciaException;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;
import sigb.modelo.Material;

public class MaterialDAOJdbc implements MaterialDAO {

    @Override
    public List<Material> buscarPorTitulo(String texto) {
        String sql = "SELECT m.id_material, m.titulo, m.autor, e.id_ejemplar, e.codigo, e.estado "
                + "FROM material m LEFT JOIN ejemplar e ON e.id_material = m.id_material "
                + "WHERE LOWER(m.titulo) LIKE LOWER(?) "
                + "ORDER BY m.titulo, e.codigo";
        try (Connection c = ConexionBD.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + (texto == null ? "" : texto.trim()) + "%");
            Map<Integer, Material> materiales = new LinkedHashMap<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idMaterial = rs.getInt("id_material");
                    Material material = materiales.get(idMaterial);
                    if (material == null) {
                        material = new Material(idMaterial, rs.getString("titulo"), rs.getString("autor"));
                        materiales.put(idMaterial, material);
                    }
                    int idEjemplar = rs.getInt("id_ejemplar");
                    if (!rs.wasNull()) {
                        material.agregarEjemplar(new Ejemplar(idEjemplar, rs.getString("codigo"),
                                idMaterial, EstadoEjemplar.desdeBD(rs.getString("estado"))));
                    }
                }
            }
            return new ArrayList<>(materiales.values());
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar el catálogo: " + e.getMessage(), e);
        }
    }
}
