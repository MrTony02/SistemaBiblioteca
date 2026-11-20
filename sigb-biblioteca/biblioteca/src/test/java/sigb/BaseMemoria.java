package sigb;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import sigb.dao.EjemplarDAO;
import sigb.dao.MaterialDAO;
import sigb.dao.MultaDAO;
import sigb.dao.PrestamoDAO;
import sigb.dao.Transaccion;
import sigb.dao.UsuarioDAO;
import sigb.modelo.Ejemplar;
import sigb.modelo.EstadoEjemplar;
import sigb.modelo.Material;
import sigb.modelo.Multa;
import sigb.modelo.Prestamo;
import sigb.modelo.Usuario;

/**
 * Implementación en memoria de todos los DAO, solo para probar los servicios sin base de datos
 * (demuestra que la capa de negocio no depende de JDBC: polimorfismo sobre las interfaces).
 */
class BaseMemoria implements UsuarioDAO, MaterialDAO, EjemplarDAO, PrestamoDAO, MultaDAO, Transaccion {

    final List<Usuario> usuarios = new ArrayList<>();
    final List<Material> materiales = new ArrayList<>();
    final List<Ejemplar> ejemplares = new ArrayList<>();
    final List<Prestamo> prestamos = new ArrayList<>();
    final List<Multa> multas = new ArrayList<>();
    private int secuencia = 100;

    @Override
    public <T> T ejecutar(Supplier<T> trabajo) {
        return trabajo.get();
    }

    @Override
    public Optional<Usuario> buscarPorDocumento(String documento) {
        return usuarios.stream().filter(u -> u.getDocumento().equals(documento)).findFirst();
    }

    @Override
    public List<Material> buscarPorTitulo(String texto) {
        return materiales.stream()
                .filter(m -> m.getTitulo().toLowerCase().contains(texto.toLowerCase()))
                .toList();
    }

    @Override
    public Optional<Ejemplar> buscarPorCodigo(String codigo, boolean bloquear) {
        return ejemplares.stream().filter(e -> e.getCodigo().equals(codigo)).findFirst();
    }

    @Override
    public void actualizarEstado(int idEjemplar, EstadoEjemplar nuevoEstado) {
        ejemplares.stream().filter(e -> e.getId() == idEjemplar).findFirst()
                .orElseThrow().actualizarEstado(nuevoEstado);
    }

    @Override
    public void guardar(Prestamo prestamo) {
        prestamo.setId(++secuencia);
        prestamos.add(prestamo);
    }

    @Override
    public Optional<Prestamo> buscarActivoPorEjemplar(int idEjemplar) {
        return prestamos.stream()
                .filter(p -> p.getIdEjemplar() == idEjemplar && p.estaActivo()).findFirst();
    }

    @Override
    public void registrarDevolucion(Prestamo prestamo) {
        // el objeto ya está en la lista y fue modificado por el servicio
    }

    @Override
    public void guardar(Multa multa) {
        multa.setId(++secuencia);
        multas.add(multa);
    }

    @Override
    public boolean tieneMultasPendientes(int idUsuario) {
        return !listarPendientesPorUsuario(idUsuario).isEmpty();
    }

    @Override
    public List<Multa> listarPendientesPorUsuario(int idUsuario) {
        return multas.stream()
                .filter(m -> !m.estaPagada())
                .filter(m -> prestamos.stream()
                        .anyMatch(p -> p.getId() == m.getIdPrestamo() && p.getIdUsuario() == idUsuario))
                .toList();
    }

    @Override
    public boolean marcarPagada(int idMulta) {
        for (Multa m : multas) {
            if (m.getId() == idMulta && !m.estaPagada()) {
                m.pagar();
                return true;
            }
        }
        return false;
    }
}
